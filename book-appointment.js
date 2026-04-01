const serviceSelect = document.getElementById("serviceSelect");
const serviceInfo = document.getElementById("serviceInfo");
const bookingForm = document.getElementById("bookingForm");

function getServices() {
    return JSON.parse(localStorage.getItem("call_services")) || [];
}

function getAppointments() {
    return JSON.parse(localStorage.getItem("call_appointments")) || [];
}

function saveAppointments(appointments) {
    localStorage.setItem("call_appointments", JSON.stringify(appointments));
}

function loadServices() {
    const services = getServices();

    serviceSelect.innerHTML = `<option value="">Choose a service</option>`;

    services.forEach((service, index) => {
        const option = document.createElement("option");
        option.value = index;
        option.textContent = service.name;
        serviceSelect.appendChild(option);
    });
}

serviceSelect.addEventListener("change", () => {
    const services = getServices();
    const selectedIndex = serviceSelect.value;

    if (selectedIndex === "") {
        serviceInfo.textContent = "Select a service to view its duration and price.";
        return;
    }

    const service = services[selectedIndex];
    serviceInfo.innerHTML = `
        <strong>Service:</strong> ${service.name}<br>
        <strong>Duration:</strong> ${service.duration} mins<br>
        <strong>Price:</strong> $${service.price}
    `;
});

bookingForm.addEventListener("submit", (e) => {
    e.preventDefault();

    const clientName = document.getElementById("clientName").value.trim();
    const date = document.getElementById("appointmentDate").value;
    const timeInput = document.getElementById("appointmentTime").value;
    const selectedIndex = serviceSelect.value;

    if (!clientName || !date || !timeInput || selectedIndex === "") {
        alert("Please complete all booking fields.");
        return;
    }

    const services = getServices();
    const selectedService = services[selectedIndex];

    if (!selectedService) {
        alert("Selected service is invalid.");
        return;
    }

    const appointments = getAppointments();

    const alreadyBooked = appointments.some(appt =>
        appt.date === date && appt.time24 === timeInput
    );

    if (alreadyBooked) {
        alert("That time slot is already booked. Please choose another time.");
        return;
    }

    const [hour, minute] = timeInput.split(":");
    const hourNum = parseInt(hour, 10);
    const ampm = hourNum >= 12 ? "PM" : "AM";
    const displayHour = hourNum % 12 || 12;
    const formattedTime = `${displayHour}:${minute} ${ampm}`;

    const newAppointment = {
        date: date,
        time: formattedTime,
        time24: timeInput,
        clientName: clientName,
        service: selectedService.name,
        duration: selectedService.duration,
        price: selectedService.price
    };

    appointments.push(newAppointment);
    saveAppointments(appointments);

    alert("Appointment booked successfully.");
    bookingForm.reset();
    serviceInfo.textContent = "Select a service to view its duration and price.";
});
loadServices();