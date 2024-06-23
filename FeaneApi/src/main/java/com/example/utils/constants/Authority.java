package com.example.utils.constants;

public enum Authority {
	READ,
	WRITE,
	UPDATE,
	USER,  //user can read,write,update,delete self object
	ADMIN  //admin can do all with any object
}
