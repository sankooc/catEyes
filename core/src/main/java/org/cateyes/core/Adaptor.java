/**
 * 
 */
package org.cateyes.core;

/**
 * @author sankooc
 * 
 */
public abstract class Adaptor {
	public <T> T getAdaptor(Class<T> clz){
		while(true){
			getClass().getInterfaces();
			Class<?> sup = this.getClass().getSuperclass();
			if(sup.equals(Object.class)){
				break;
			}
			if(sup.equals(clz)){
				return (T) this;
			}
		}
		return null;
	}
}
