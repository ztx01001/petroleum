package com.university.springboot_petroleum.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.university.springboot_petroleum.domain.ResponseMessage;
import com.university.springboot_petroleum.queue.ResponseMessageQueue;
import com.university.springboot_petroleum.util.ByteUtil;
import com.university.springboot_petroleum.util.Constants;
import com.university.springboot_petroleum.util.StringUtil;



public class TCPRequestClient {

	private static Logger log = LoggerFactory.getLogger(TCPRequestClient.class);
	//tcp server端的ip地址
	private String host;
	//tcp server端的端口号
	private int port;
	//private byte[] reqData;
	//
	private Socket socket = null;
	private OutputStream out = null;
	private InputStream in = null;
	public TCPRequestClient(String host,int port) {
		this.host = host;
		this.port = port;		
	}
	//建立socket链接
	private boolean connect() {
		boolean result = true;
		try {
			log.info("tcp connect enter");
			//入参检查
			if(host ==null || host.isEmpty() || port == 0) {
				log.error("host is null or empty, port is zero");
				return false;
			}
			//创建串口转以太网口socket连接
			socket = new Socket(host,port);
			//设置socket连接为立即发送
			socket.setTcpNoDelay(true);
			//设置socket连接读超时时间
			socket.setSoTimeout(2*1000);
			//建立连接后，获取输出流输入流
			out = socket.getOutputStream();
			in = socket.getInputStream();
			log.info("tcp connect end");
		}catch(Exception e) {
			log.error("TCPRequestClient connect error="+e.getLocalizedMessage());
			return false;
		}
		return result;
	}
	//发送请求，接收返回，一次接收完成的情景
	public void sendReqNoWait(byte[] req) {
		byte[] result = null;
		if(req == null || req.length == Constants.Zero) {
			log.error("sendReqNoWait request data is null");
			return ;
		}
		try {
			boolean conbool = connect();
			int len  = 0;			
			if(conbool) {
				log.info("tcp connect is ok start to write");
				//将请求写channel
				out.write(req);
				//立即发送
				out.flush();
				//关闭socket输出流,向服务端发送发送结束标识符
				socket.shutdownOutput();
				result = new byte[256];
				//如果服务端不主动断链，等待读超时,流末尾时为-1 只读一次
				log.info("start receive ----");
				while((len = in.read(result)) != Constants.InputStreamEnd) {
					log.info("receive enter----len="+len);
					if(len > 0) {
						log.info("TCPRequestClient sendReqNoWait get response len="+len);
						ByteUtil.printHex(result,len);
						byte[] data = new byte[len];
						System.arraycopy(result,Constants.Zero,data,Constants.Zero,len);						
						ResponseMessage response = new ResponseMessage(data,len,host);
						ResponseMessageQueue.putResponse(response);
						result = null;
						break;
					}
					
				}
				
				log.info("end receive ----");
			}
		}catch(Exception e) {
			log.error("sendReqNoWait exception ="+e.getLocalizedMessage());
		}finally {
			releaseResource();
		}
		
		
		return ;
	}
	
	    //发送请求，接收返回，多次接收 需要读超时完成的情景
		public void sendReqWait(byte[] req) {
			log.info("sendReqWait read timeout enter");
			byte[] result = null;
			if(req == null || req.length == Constants.Zero) {
				log.error("sendReqNoWait request data is null");
				
			}
			
			int len  = 0;
			int dataLen = 0;
			
			try {
				//boolean conbool = connect();
				log.info("tcp connect enter");
				//入参检查
				if(host ==null || host.isEmpty() || port == 0) {
					log.error("host is null or empty, port is zero");
					
				}
				log.info("host="+host+"  port"+port);
				//创建串口转以太网口socket连接
				socket = new Socket(host,port);
				//设置socket连接为立即发送
				socket.setTcpNoDelay(true);
				//设置socket连接读超时时间
				socket.setSoTimeout(4*1000);
				//建立连接后，获取输出流输入流
				out = socket.getOutputStream();
				in = socket.getInputStream();
				log.info("connect end start write data ");
				
					//将请求写道channel
					out.write(req);
					//立即发送
					out.flush();
					//关闭socket输出流,向服务端发送发送结束标识符
					//socket.shutdownOutput();
					log.info("data flush end start read");
					byte[] data = new byte[Constants.PduLen];
					result = new byte[Constants.PduLen];
					//如果服务端不主动断链，等待读超时,流末尾时为-1 只读一次
					log.info("read start-------");
					while(true) {
						log.info("read enter");
						len = in.read(data);
						if(len >0) {
							log.info("read len="+len+"  data="+StringUtil.bytes2Hex(data));
							System.arraycopy(data, 0, result, dataLen, len);
						}else {
							log.info("read len="+len);
						}
						
						
						dataLen += len;
						log.info("copy data into result dataLen="+dataLen);
						if(dataLen >=Constants.PduLen) {
							break;
						}
					}
					ResponseMessage response = new ResponseMessage(result,dataLen,host);
					ResponseMessageQueue.putResponse(response);
//					while((len = in.read(data)) != Constants.InputStreamEnd) {
//						log.info("always read until timeout");
//						System.arraycopy(data, 0, result, destPos, len);
//						destPos += len;
//					}
					log.info("read end");
				
			}catch(SocketTimeoutException e) {
				//读超时异常时，将最后接收的结果返回
				log.error("SocketTimeoutException exception ="+e.getLocalizedMessage());
				if(0 != dataLen ) {
					log.info("byte len="+dataLen+" data="+StringUtil.bytes2Hex(result));
					ResponseMessage response = new ResponseMessage(result,dataLen,host);
					ResponseMessageQueue.putResponse(response);
					
				}
			}catch(SocketException e) {
				log.error("SocketException enter");
			}catch(IOException e) {
				log.error("IOException enter");
			}catch(Exception e) {
				log.error("Exception enter");
			}finally {
				releaseResource();
			}		
			
			
		}
	
	private void releaseResource() {
		log.info("TCPRequestClient releaseResource ");
		try {
			if(out != null)
				out.close();
			if(in != null)
				in.close();
			if(socket != null)
				socket.close();
		}catch(Exception e) {
			log.error("sendReqNoWait finally release resource exception="+e.getLocalizedMessage());
		}
	}
}
