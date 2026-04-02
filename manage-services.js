function getServices() {
    return JSON.parse(localStorage.getItem("call_services")) || [];
}

function saveServices(services) {
    localStorage.setItem("call_services", JSON.stringify(services));
}

function renderServices() {
    const services = getServices();
    const tableBody = document.querySelector("#servicesTable tbody");
    tableBody.innerHTML = "";

    services.forEach((service, index) => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${service.name}</td>
            <td>${service.duration} mins</td>
            <td>$${service.price}</td>
            <td>
                <button class="action-btn delete-btn" onclick="deleteService(${index})">Delete</button>
            </td>
        `;

        tableBody.appendChild(row);
    });
}

function addService() {
    const name = document.getElementById("serviceName").value.trim();
    const duration = document.getElementById("serviceDuration").value.trim();
    const price = document.getElementById("servicePrice").value.trim();

    if (!name || !duration || !price) {
        alert("Please complete all service fields.");
        return;
    }

    const services = getServices();
    services.push({
        name,
        duration,
        price
    });

    saveServices(services);
    renderServices();

    document.getElementById("serviceName").value = "";
    document.getElementById("serviceDuration").value = "";
    document.getElementById("servicePrice").value = "";
}

function deleteService(index) {
    const services = getServices();
    services.splice(index, 1);
    saveServices(services);
    renderServices();
}

renderServices();
