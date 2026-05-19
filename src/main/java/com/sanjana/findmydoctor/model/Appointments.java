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
public class Appointments {
	private int id;
	private String doctorEmail;
	private String userEmail;
	private String name;
	private String status;
	
	private Date docBookingDate; //Appointment Day
	private String docBookingTime;
	
	private Date bookingDateTime; //day of booking
	
}
