const calendarGrid = document.getElementById("calendarGrid");
const monthTitle = document.getElementById("monthTitle");
const appointmentsContainer = document.getElementById("appointmentsContainer");
const selectedDateTitle = document.getElementById("selectedDateTitle");

let currentDate = new Date();
let selectedDate = null;

// Demo appointments for now
function seedAppointmentsIfNeeded() {
    const existing = localStorage.getItem("call_appointments");
    if (!existing) {
        const today = new Date();
        const y = today.getFullYear();
        const m = String(today.getMonth() + 1).padStart(2, "0");

        const demoAppointments = [
            {
                date: `${y}-${m}-05`,
                time: "10:00 AM",
                clientName: "John Brown",
                service: "Haircut"
            },
            {
                date: `${y}-${m}-05`,
                time: "1:00 PM",
                clientName: "Sarah Smith",
                service: "Braiding"
            },
            {
                date: `${y}-${m}-12`,
                time: "11:30 AM",
                clientName: "Kevin Johnson",
                service: "Hair Wash"
            },
            {
                date: `${y}-${m}-18`,
                time: "3:00 PM",
                clientName: "Alicia Green",
                service: "Haircut"
            }
        ];

        localStorage.setItem("call_appointments", JSON.stringify(demoAppointments));
    }
}

function getAppointments() {
    return JSON.parse(localStorage.getItem("call_appointments")) || [];
}

function formatDateKey(year, month, day) {
    return `${year}-${String(month + 1).padStart(2, "0")}-${String(day).padStart(2, "0")}`;
}

function renderCalendar() {
    calendarGrid.innerHTML = "";

    const dayNames = ["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"];
    dayNames.forEach(day => {
        const dayNameEl = document.createElement("div");
        dayNameEl.className = "calendar-day-name";
        dayNameEl.textContent = day;
        calendarGrid.appendChild(dayNameEl);
    });

    const year = currentDate.getFullYear();
    const month = currentDate.getMonth();

    monthTitle.textContent = currentDate.toLocaleString("default", {
        month: "long",
        year: "numeric"
    });

    const firstDay = new Date(year, month, 1).getDay();
    const totalDays = new Date(year, month + 1, 0).getDate();

    for (let i = 0; i < firstDay; i++) {
        const empty = document.createElement("div");
        empty.className = "calendar-day empty";
        calendarGrid.appendChild(empty);
    }

    const appointments = getAppointments();

    for (let day = 1; day <= totalDays; day++) {
        const dateKey = formatDateKey(year, month, day);
        const dayAppointments = appointments.filter(appt => appt.date === dateKey);

        const dayEl = document.createElement("div");
        dayEl.className = "calendar-day";
        dayEl.textContent = day;

        if (dayAppointments.length > 0) {
            dayEl.classList.add("booked");
        } else {
            dayEl.classList.add("free");
        }

        if (selectedDate === dateKey) {
            dayEl.classList.add("selected");
        }

        dayEl.addEventListener("click", () => {
            selectedDate = dateKey;
            renderCalendar();
            renderAppointmentsForDay(dateKey);
        });

        calendarGrid.appendChild(dayEl);
    }
}

function renderAppointmentsForDay(dateKey) {
    const appointments = getAppointments().filter(appt => appt.date === dateKey);

    selectedDateTitle.textContent = `Appointments for ${dateKey}`;
    appointmentsContainer.innerHTML = "";

    if (appointments.length === 0) {
        appointmentsContainer.innerHTML = `<p class="empty-text">No appointments for this day.</p>`;
        return;
    }

    appointments.forEach(appt => {
        const card = document.createElement("div");
        card.className = "appointment-card";
    card.innerHTML = `
        <h3>${appt.service}</h3>
        <p><strong>Client:</strong> ${appt.clientName}</p>
        <p><strong>Time:</strong> ${appt.time}</p>
        <p><strong>Duration:</strong> ${appt.duration || "N/A"} mins</p>
        <p><strong>Price:</strong> $${appt.price || "N/A"}</p>
`;
        appointmentsContainer.appendChild(card);
    });
}

function changeMonth(direction) {
    currentDate.setMonth(currentDate.getMonth() + direction);
    selectedDate = null;
    renderCalendar();
    appointmentsContainer.innerHTML = `<p class="empty-text">Click a day to view appointments.</p>`;
    selectedDateTitle.textContent = "Appointments";
}

seedAppointmentsIfNeeded();
renderCalendar();