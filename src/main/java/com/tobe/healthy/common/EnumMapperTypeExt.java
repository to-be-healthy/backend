package com.tobe.healthy.common;

public interface EnumMapperTypeExt extends EnumMapperType{
	public <T extends Enum<T> & EnumMapperType> T getParentCode();
}
