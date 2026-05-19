/*// ============================================
// FIND MY DOCTOR - MAIN JAVASCRIPT
// ============================================

// Sample Data
const doctorsDatabase = [
    {
        id: 1,
        name: "Dr. Rajesh Kumar",
        specialty: "Cardiology",
        location: "Mumbai",
        rating: 4.8,
        reviews: 245,
        fee: 500,
        experience: "15 years",
        qualifications: "MD, DM (Cardiology)",
        availability: "Mon-Fri 10AM-6PM",
        image: "👨‍⚕️"
    },
    {
        id: 2,
        name: "Dr. Priya Singh",
        specialty: "Dermatology",
        location: "Delhi",
        rating: 4.9,
        reviews: 312,
        fee: 400,
        experience: "12 years",
        qualifications: "MBBS, MD (Dermatology)",
        availability: "Mon-Sat 9AM-5PM",
        image: "👩‍⚕️"
    },
    {
        id: 3,
        name: "Dr. Arjun Patel",
        specialty: "Orthopedics",
        location: "Bangalore",
        rating: 4.7,
        reviews: 198,
        fee: 450,
        experience: "10 years",
        qualifications: "MBBS, MS (Orthopedics)",
        availability: "Tue-Sat 2PM-8PM",
        image: "👨‍⚕️"
    },
    {
        id: 4,
        name: "Dr. Anjali Verma",
        specialty: "Neurology",
        location: "Pune",
        rating: 4.6,
        reviews: 156,
        fee: 550,
        experience: "14 years",
        qualifications: "MD, DM (Neurology)",
        availability: "Mon-Thu 11AM-6PM",
        image: "👩‍⚕️"
    },
    {
        id: 5,
        name: "Dr. Vikram Shah",
        specialty: "Pediatrics",
        location: "Mumbai",
        rating: 4.8,
        reviews: 287,
        fee: 350,
        experience: "11 years",
        qualifications: "MBBS, DCH",
        availability: "Mon-Fri 9AM-5PM",
        image: "👨‍⚕️"
    },
    {
        id: 6,
        name: "Dr. Neha Gupta",
        specialty: "Gynecology",
        location: "Delhi",
        rating: 4.9,
        reviews: 425,
        fee: 480,
        experience: "13 years",
        qualifications: "MBBS, MD (Obs & Gynae)",
        availability: "Daily 10AM-6PM",
        image: "👩‍⚕️"
    }
];

const symptomsDatabase = {
    "Headache": ["Migraine", "Tension Headache", "Cluster Headache"],
    "Fever": ["Influenza", "Common Cold", "Dengue"],
    "Cough": ["Bronchitis", "Pneumonia", "Asthma"],
    "Chest Pain": ["Angina", "Cardiac Arrhythmia", "Acid Reflux"],
    "Abdominal Pain": ["Gastroenteritis", "Appendicitis", "Ulcer"],
    "Joint Pain": ["Arthritis", "Osteoarthritis", "Gout"],
    "Skin Rash": ["Eczema", "Psoriasis", "Dermatitis"],
    "Shortness of Breath": ["Asthma", "COPD", "Heart Failure"]
};

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    initializePage();
    attachEventListeners();
});

// Initialize page based on current URL
function initializePage() {
    const path = window.location.pathname;
    
    if (path.includes('search-doctor')) {
        initializeSearchDoctor();
    } else if (path.includes('login')) {
        initializeLoginPage();
    } else if (path.includes('symptom-checker')) {
        initializeSymptomChecker();
    } else if (path.includes('appointments')) {
        initializeAppointments();
    } else if (path.includes('about')) {
        initializeAbout();
    } else if (path.includes('contact')) {
        initializeContact();
    }
}

// Attach global event listeners
function attachEventListeners() {
    // Mobile menu close on link click
    const navLinks = document.querySelectorAll('.navbar-collapse .nav-link');
    navLinks.forEach(link => {
        link.addEventListener('click', function() {
            const navbarCollapse = document.querySelector('.navbar-collapse');
            if (navbarCollapse.classList.contains('show')) {
                document.querySelector('.navbar-toggler').click();
            }
        });
    });
}

// ============================================
// SEARCH DOCTOR PAGE
// ============================================

function initializeSearchDoctor() {
    displayDoctors(doctorsDatabase);
    attachSearchFilters();
}

function displayDoctors(doctors) {
    const container = document.getElementById('doctors-container');
    if (!container) return;

    container.innerHTML = '';
    
    if (doctors.length === 0) {
        container.innerHTML = '<div class="alert alert-info text-center w-100">No doctors found matching your criteria. Please try different filters.</div>';
        return;
    }

    doctors.forEach(doctor => {
        const doctorCard = createDoctorCard(doctor);
        container.appendChild(doctorCard);
    });
}

function createDoctorCard(doctor) {
    const card = document.createElement('div');
    card.className = 'col-md-6 col-lg-4';
    card.innerHTML = `
        <div class="doctor-card">
            <div class="doctor-header">
                <div class="doctor-avatar">${doctor.image}</div>
                <div>
                    <h5>${doctor.name}</h5>
                    <p class="doctor-specialty">${doctor.specialty}</p>
                </div>
            </div>
            <div class="doctor-body">
                <div class="rating">
                    ${'⭐'.repeat(Math.floor(doctor.rating))} ${doctor.rating} (${doctor.reviews} reviews)
                </div>
                <div class="doctor-details">
                    <div class="doctor-detail">
                        <i class="fas fa-map-marker-alt"></i>
                        <span>${doctor.location}</span>
                    </div>
                    <div class="doctor-detail">
                        <i class="fas fa-graduation-cap"></i>
                        <span>${doctor.qualifications}</span>
                    </div>
                    <div class="doctor-detail">
                        <i class="fas fa-briefcase"></i>
                        <span>${doctor.experience}</span>
                    </div>
                    <div class="doctor-detail">
                        <i class="fas fa-clock"></i>
                        <span>${doctor.availability}</span>
                    </div>
                    <div class="doctor-detail">
                        <i class="fas fa-rupee-sign"></i>
                        <span>₹${doctor.fee} per consultation</span>
                    </div>
                </div>
            </div>
            <div class="doctor-footer">
                <button class="btn btn-primary" onclick="bookAppointment(${doctor.id})">Book Appointment</button>
                <button class="btn btn-outline-primary" onclick="videoConsult(${doctor.id})">Video Call</button>
            </div>
        </div>
    `;
    return card;
}

function attachSearchFilters() {
    const searchBtn = document.getElementById('search-btn');
    const locationFilter = document.getElementById('location-filter');
    const specialtyFilter = document.getElementById('specialty-filter');
    const ratingFilter = document.getElementById('rating-filter');

    if (!searchBtn) return;

    searchBtn.addEventListener('click', applyFilters);
    
    // Apply filters on input change
    if (locationFilter) locationFilter.addEventListener('change', applyFilters);
    if (specialtyFilter) specialtyFilter.addEventListener('change', applyFilters);
    if (ratingFilter) ratingFilter.addEventListener('change', applyFilters);
}

function applyFilters() {
    const location = document.getElementById('location-filter')?.value || '';
    const specialty = document.getElementById('specialty-filter')?.value || '';
    const rating = parseFloat(document.getElementById('rating-filter')?.value || 0);

    let filtered = doctorsDatabase;

    if (location) {
        filtered = filtered.filter(doc => doc.location.toLowerCase().includes(location.toLowerCase()));
    }

    if (specialty) {
        filtered = filtered.filter(doc => doc.specialty.toLowerCase().includes(specialty.toLowerCase()));
    }

    if (rating > 0) {
        filtered = filtered.filter(doc => doc.rating >= rating);
    }

    displayDoctors(filtered);
}

function bookAppointment(doctorId) {
    const doctor = doctorsDatabase.find(d => d.id === doctorId);
    if (!doctor) return;

    // Store selected doctor in sessionStorage
    sessionStorage.setItem('selectedDoctor', JSON.stringify(doctor));
    sessionStorage.setItem('consultationType', 'in-person');

    // Show appointment booking modal
    showBookingModal(doctor, 'in-person');
}

function videoConsult(doctorId) {
    const doctor = doctorsDatabase.find(d => d.id === doctorId);
    if (!doctor) return;

    sessionStorage.setItem('selectedDoctor', JSON.stringify(doctor));
    sessionStorage.setItem('consultationType', 'video');

    showBookingModal(doctor, 'video');
}

function showBookingModal(doctor, type) {
    const modal = new bootstrap.Modal(document.getElementById('bookingModal') || createBookingModal(doctor, type));
    modal.show();
}

function createBookingModal(doctor, type) {
    const modal = document.createElement('div');
    modal.className = 'modal fade';
    modal.id = 'bookingModal';
    modal.innerHTML = `
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header bg-primary text-white">
                    <h5 class="modal-title">Book ${type === 'video' ? 'Video' : 'In-Person'} Consultation</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <div class="modal-body">
                    <p><strong>Doctor:</strong> ${doctor.name}</p>
                    <p><strong>Specialty:</strong> ${doctor.specialty}</p>
                    <p><strong>Type:</strong> ${type === 'video' ? 'Video Consultation' : 'In-Person Appointment'}</p>
                    <div class="form-group">
                        <label>Select Date:</label>
                        <input type="date" id="appointmentDate" class="form-control" min="${new Date().toISOString().split('T')[0]}">
                    </div>
                    <div class="form-group">
                        <label>Select Time:</label>
                        <select id="appointmentTime" class="form-control">
                            <option>09:00 AM</option>
                            <option>10:00 AM</option>
                            <option>11:00 AM</option>
                            <option>02:00 PM</option>
                            <option>03:00 PM</option>
                            <option>04:00 PM</option>
                            <option>05:00 PM</option>
                        </select>
                    </div>
                    <div class="form-group">
                        <label>Notes (Optional):</label>
                        <textarea id="appointmentNotes" class="form-control" rows="3"></textarea>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="button" class="btn btn-primary" onclick="confirmBooking('${doctor.id}', '${type}')">Confirm Booking</button>
                </div>
            </div>
        </div>
    `;
    document.body.appendChild(modal);
    return modal;
}

function confirmBooking(doctorId, type) {
    const date = document.getElementById('appointmentDate')?.value;
    const time = document.getElementById('appointmentTime')?.value;
    const notes = document.getElementById('appointmentNotes')?.value || '';

    if (!date || !time) {
        alert('Please select both date and time');
        return;
    }

    const doctor = doctorsDatabase.find(d => d.id == doctorId);
    const appointment = {
        id: Math.random().toString(36).substr(2, 9),
        doctorName: doctor.name,
        doctorSpecialty: doctor.specialty,
        consultationType: type,
        date: date,
        time: time,
        status: 'pending',
        notes: notes,
        createdAt: new Date().toISOString()
    };

    // Save to localStorage
    let appointments = JSON.parse(localStorage.getItem('appointments')) || [];
    appointments.push(appointment);
    localStorage.setItem('appointments', JSON.stringify(appointments));

    // Close modal and show success message
    const modal = bootstrap.Modal.getInstance(document.getElementById('bookingModal'));
    modal?.hide();

    showSuccessAlert(`Appointment booked successfully! Your appointment with ${doctor.name} is scheduled for ${date} at ${time}.`);

    // Clear the appointment modal after booking
    setTimeout(() => {
        const modalEl = document.getElementById('bookingModal');
        if (modalEl) modalEl.remove();
    }, 2000);
}

// ============================================
// LOGIN PAGE
// ============================================

function initializeLoginPage() {
    const userTypeButtons = document.querySelectorAll('[data-user-type]');
    const loginForm = document.getElementById('loginForm');

    userTypeButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            userTypeButtons.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            sessionStorage.setItem('userType', this.dataset.userType);
        });
    });

    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }
}

function handleLogin(e) {
    e.preventDefault();

    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;
    const userType = sessionStorage.getItem('userType') || 'patient';

    if (!email || !password) {
        showErrorAlert('Please fill in all fields');
        return;
    }

    // Simulate login
    const user = {
        email: email,
        userType: userType,
        loginTime: new Date().toISOString()
    };

    localStorage.setItem('currentUser', JSON.stringify(user));
    sessionStorage.setItem('authToken', 'token_' + Math.random().toString(36).substr(2, 9));

    showSuccessAlert('Login successful! Redirecting...');

    setTimeout(() => {
        switch(userType) {
            case 'patient':
                window.location.href = 'search-doctor.html';
                break;
            case 'doctor':
                window.location.href = 'doctor-dashboard.html';
                break;
            case 'admin':
                window.location.href = 'admin-dashboard.html';
                break;
            default:
                window.location.href = 'index.html';
        }
    }, 1500);
}

function handleSignup(e) {
    e.preventDefault();

    const fullName = document.getElementById('fullName').value;
    const email = document.getElementById('signupEmail').value;
    const password = document.getElementById('signupPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const userType = sessionStorage.getItem('userType') || 'patient';

    if (!fullName || !email || !password || !confirmPassword) {
        showErrorAlert('Please fill in all fields');
        return;
    }

    if (password !== confirmPassword) {
        showErrorAlert('Passwords do not match');
        return;
    }

    // Simulate signup
    const user = {
        fullName: fullName,
        email: email,
        userType: userType,
        signupTime: new Date().toISOString()
    };

    localStorage.setItem('currentUser', JSON.stringify(user));

    showSuccessAlert('Signup successful! Redirecting to login...');

    setTimeout(() => {
        window.location.href = 'login.html';
    }, 1500);
}

// ============================================
// SYMPTOM CHECKER PAGE
// ============================================

// function initializeSymptomChecker() {
//     displaySymptoms();
//     attachSymptomListeners();
//     const checkBtn = document.getElementById('checkBtn');
//     if (checkBtn) {
//         checkBtn.addEventListener('click', checkDiseases);
//     }
// }

// function displaySymptoms() {
//     const container = document.getElementById('symptoms-container');
//     if (!container) return;

//     container.innerHTML = '';
//     Object.keys(symptomsDatabase).forEach(symptom => {
//         const item = document.createElement('div');
//         item.className = 'symptom-item';
//         item.innerHTML = `
//             <input type="checkbox" id="symptom-${symptom}" value="${symptom}" class="symptom-checkbox">
//             <label for="symptom-${symptom}">${symptom}</label>
//         `;
//         container.appendChild(item);
//     });
// }

// function attachSymptomListeners() {
//     const checkboxes = document.querySelectorAll('.symptom-checkbox');
//     checkboxes.forEach(checkbox => {
//         checkbox.addEventListener('change', function() {
//             this.parentElement.classList.toggle('active', this.checked);
//         });
//     });
// }

// function checkDiseases() {
//     const selected = Array.from(document.querySelectorAll('.symptom-checkbox:checked'))
//         .map(cb => cb.value);

//     if (selected.length === 0) {
//         showErrorAlert('Please select at least one symptom');
//         return;
//     }

//     const diseases = new Set();
//     selected.forEach(symptom => {
//         symptomsDatabase[symptom]?.forEach(disease => diseases.add(disease));
//     });

//     displayDiseaseResults(Array.from(diseases), selected);
// }

// function displayDiseaseResults(diseases, symptoms) {
//     const container = document.getElementById('results-container');
//     if (!container) return;

//     container.innerHTML = `
//         <div class="alert alert-warning">
//             <strong>⚠️ Disclaimer:</strong> This is a symptom-based screening tool and NOT a substitute for professional medical diagnosis. Please consult a doctor for accurate diagnosis and treatment.
//         </div>
//     `;

//     diseases.forEach(disease => {
//         const result = document.createElement('div');
//         result.className = 'disease-result';
//         result.innerHTML = `
//             <h4><i class="fas fa-exclamation-triangle"></i> ${disease}</h4>
//             <p>Based on your symptoms: <strong>${symptoms.join(', ')}</strong></p>
//             <p>This could be related to <strong>${disease}</strong>. We recommend consulting with a specialist.</p>
//             <button class="btn btn-primary" onclick="findDoctorForDisease('${disease}')">
//                 Find Doctor for ${disease}
//             </button>
//         `;
//         container.appendChild(result);
//     });
// }

// function findDoctorForDisease(disease) {
//     sessionStorage.setItem('searchSpecialty', disease);
//     window.location.href = 'search-doctor.html';
// }

// ============================================
// APPOINTMENTS PAGE
// ============================================

function initializeAppointments() {
    displayAppointments();
}

function displayAppointments() {
    const container = document.getElementById('appointments-container');
    if (!container) return;

    const appointments = JSON.parse(localStorage.getItem('appointments')) || [];

    if (appointments.length === 0) {
        container.innerHTML = '<div class="alert alert-info text-center w-100">No appointments booked yet. <a href="search-doctor.html">Book an appointment</a></div>';
        return;
    }

    container.innerHTML = '';
    appointments.forEach(apt => {
        const card = createAppointmentCard(apt);
        container.appendChild(card);
    });
}

function createAppointmentCard(appointment) {
    const card = document.createElement('div');
    card.className = 'appointment-card';
    const statusClass = `status-${appointment.status}`;
    
    card.innerHTML = `
        <div class="appointment-header">
            <div>
                <div class="appointment-doctor">${appointment.doctorName}</div>
                <small>${appointment.doctorSpecialty}</small>
            </div>
            <span class="appointment-status ${statusClass}">${appointment.status.charAt(0).toUpperCase() + appointment.status.slice(1)}</span>
        </div>
        <div class="appointment-body">
            <div class="appointment-detail">
                <i class="fas fa-calendar"></i>
                <span>${appointment.date}</span>
            </div>
            <div class="appointment-detail">
                <i class="fas fa-clock"></i>
                <span>${appointment.time}</span>
            </div>
            <div class="appointment-detail">
                <i class="fas fa-${appointment.consultationType === 'video' ? 'video' : 'hospital'}"></i>
                <span>${appointment.consultationType === 'video' ? 'Video Consultation' : 'In-Person Appointment'}</span>
            </div>
            ${appointment.notes ? `<div class="appointment-detail"><i class="fas fa-note"></i><span>Notes: ${appointment.notes}</span></div>` : ''}
        </div>
        <div class="appointment-actions">
            <button class="btn btn-primary btn-sm" onclick="rescheduleAppointment('${appointment.id}')">Reschedule</button>
            <button class="btn btn-danger btn-sm" onclick="cancelAppointment('${appointment.id}')">Cancel</button>
        </div>
    `;
    return card;
}

function cancelAppointment(appointmentId) {
    if (confirm('Are you sure you want to cancel this appointment?')) {
        let appointments = JSON.parse(localStorage.getItem('appointments')) || [];
        appointments = appointments.filter(apt => apt.id !== appointmentId);
        localStorage.setItem('appointments', JSON.stringify(appointments));
        
        showSuccessAlert('Appointment cancelled successfully');
        setTimeout(() => {
            location.reload();
        }, 1500);
    }
}

function rescheduleAppointment(appointmentId) {
    alert('Reschedule appointment feature coming soon!');
}

// ============================================
// ABOUT PAGE
// ============================================

function initializeAbout() {
    // Add scroll animations if needed
    console.log('About page initialized');
}

// ============================================
// CONTACT PAGE
// ============================================

function initializeContact() {
    const contactForm = document.getElementById('contactForm');
    if (contactForm) {
        contactForm.addEventListener('submit', handleContactForm);
    }
}

function handleContactForm(e) {
    e.preventDefault();

    const name = document.getElementById('name').value;
    const email = document.getElementById('email').value;
    const subject = document.getElementById('subject').value;
    const message = document.getElementById('message').value;

    if (!name || !email || !subject || !message) {
        showErrorAlert('Please fill in all fields');
        return;
    }

    // Simulate form submission
    const contact = {
        name: name,
        email: email,
        subject: subject,
        message: message,
        submittedAt: new Date().toISOString()
    };

    let contacts = JSON.parse(localStorage.getItem('contacts')) || [];
    contacts.push(contact);
    localStorage.setItem('contacts', JSON.stringify(contacts));

    showSuccessAlert('Thank you for your message! We will get back to you soon.');
    e.target.reset();
}

// ============================================
// UTILITY FUNCTIONS
// ============================================

function showSuccessAlert(message) {
    showAlert(message, 'success');
}

function showErrorAlert(message) {
    showAlert(message, 'danger');
}

function showAlert(message, type = 'info') {
    const alertDiv = document.createElement('div');
    alertDiv.className = `alert alert-${type} alert-dismissible fade show`;
    alertDiv.role = 'alert';
    alertDiv.innerHTML = `
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    const container = document.querySelector('.container');
    if (container) {
        container.parentElement.insertBefore(alertDiv, container);
    } else {
        document.body.insertBefore(alertDiv, document.body.firstChild);
    }

    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

function logout() {
    localStorage.removeItem('currentUser');
    sessionStorage.clear();
    window.location.href = 'index.html';
}

// Check if user is logged in
function checkLogin() {
    const user = JSON.parse(localStorage.getItem('currentUser'));
    return user !== null;
}

// Get current user
function getCurrentUser() {
    return JSON.parse(localStorage.getItem('currentUser'));
}
*/