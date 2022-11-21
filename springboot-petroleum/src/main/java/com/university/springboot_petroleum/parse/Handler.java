package com.university.springboot_petroleum.parse;

import com.university.springboot_petroleum.domain.ResponseMessage;

/*
 * 责任链模式，第一种方式：处理完一个，继续下一个，不管前面怎么处理。第二种方式：处理完一个，成功的话，下一个；否则返回
 */
public abstract class Handler {

	//下一个处理器
	protected Handler next = null;
	//this 关注处理校验的业务
	public abstract boolean doHandler(ResponseMessage message);
	//添加下一个处理器的方法
	public void next(Handler handler) {
		this.next = handler;
	}
	//build 模式
	public static class Builder {
		private Handler header = null;
		private Handler tail = null;
		public Builder add(Handler handler) {
			if(this.header == null) {
				this.header = this.tail = handler;
			}else {
				tail.next = handler;
				tail = handler;
			}
			return this;
		}
		public Handler build() {
			return this.header;
		}
	}
	
}
