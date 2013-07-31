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
		Class<?> cl = getClass();
		while(true){
			Class<?> sup = cl.getSuperclass();
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
