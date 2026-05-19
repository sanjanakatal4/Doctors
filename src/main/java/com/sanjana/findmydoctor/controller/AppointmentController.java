package com.sanjana.findmydoctor.controller;

import java.util.Date;
import java.util.List;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sanjana.findmydoctor.model.Appointments;
import com.sanjana.findmydoctor.model.Doctor;
import com.sanjana.findmydoctor.model.User;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/appointment")
public class AppointmentController {
	
	String URL="http://localhost:9091/";
	RestTemplate restTemplate=new RestTemplate();
	
	@PostMapping("/addAppointment")
	public String addAppointment(@ModelAttribute Appointments	appointment,HttpSession session,RedirectAttributes ra) {
		appointment.setUserEmail(((User)session.getAttribute("user")).getEmail());
		appointment.setBookingDateTime(new java.sql.Date(new Date().getTime()));
		String API="appointment/addAppointment";
		String result=restTemplate.postForObject(URL+API, appointment, String.class);
		ra.addFlashAttribute("msg",result);

		API="appointment/getByUserEmail/"+((User)session.getAttribute("user")).getEmail();
		List<Appointments> appointments=restTemplate.getForObject(URL+API,List.class);
		ra.addFlashAttribute("apts",appointments);
		return "redirect:/user/user-appointments";
	}
	@GetMapping("/getByUserEmail")
	public String getByUserEmail(HttpSession session,ModelMap model) {
		String API="appointment/getByUserEmail/"+((User)session.getAttribute("user")).getEmail();
		List<Appointments> appointments=restTemplate.getForObject(URL+API,List.class);
		model.addAttribute("apts",appointments);
		return "redirect:/user/user-appointments";
	}
	@GetMapping("/getByDoctorEmail")
	public String getByDoctorEmail(HttpSession session,ModelMap model) {
		String API="appointment/getByDoctorEmail/"+((Doctor)session.getAttribute("doctor")).getEmail();
		List<Appointments> appointments=restTemplate.getForObject(URL+API,List.class);
		model.addAttribute("apts",appointments);
		
		return "DoctorAppointments";
	}
	@PostMapping("/statusUpdate")
	public String statusUpdate(@RequestParam String role,@RequestParam int id,@RequestParam String status) {
		String API="appointment/updateAppointmentStatus/"+id+"/"+status;
		ResponseEntity<Boolean> result= restTemplate.exchange(URL+API, HttpMethod.PUT, null,Boolean.class);
		if(role.equalsIgnoreCase("user"))
			return "redirect:/user/user-appointments";
		else
			return "redirect:/doctor/doctor-appointments";
	}
	
}
