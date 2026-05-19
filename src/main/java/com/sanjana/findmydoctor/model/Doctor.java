package com.sanjana.findmydoctor.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


//----lombok
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Doctor {
	private String email;
	private String password;
	private String name;
	private String state;
	private String city;
	private String area;
	private String speciality;
	private int fee;
	
	private DoctorDetails doctorDetails;
	private DoctorAvail doctorAvail;
	
}
