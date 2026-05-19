package com.sanjana.findmydoctor.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIResponse {
	private String message;
	private String warnings;
	private String[] topRelatedSpecialties;
	private String[] recommendations;
    private String requiresPhysicianConsult;
    private String emergencyLevel;
}
