package com.sanjana.findmydoctor.model;

import java.sql.Date;

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
public class DoctorNotAvail {
	private int id;
	
	private String doctorEmail;
	//@Temporal(TemporalType.DATE) //Optional
	private Date docDate;
	
}
