document.addEventListener("DOMContentLoaded", function () {
    const colors = ['#007bff', '#28a745', '#ffc107', '#17a2b8']; // Example color palette

    const ctx = document.getElementById("chLine").getContext("2d");
    const chartData = {
        labels: ["S", "M", "T", "W", "T", "F", "S"],
        datasets: [
            {
                label: "Employees",
                data: [589, 445, 483, 503, 689, 692, 634],
                backgroundColor: 'transparent',
                borderColor: colors[0],
                borderWidth: 3,
                pointBackgroundColor: colors[0],
                tension: 0.3
            },
            {
                label: "Projects",
                data: [639, 465, 493, 478, 589, 632, 674],
                backgroundColor: 'transparent',
                borderColor: colors[1],
                borderWidth: 3,
                pointBackgroundColor: colors[1],
                tension: 0.3
            }
        ]
    };

    new Chart(ctx, {
        type: 'line',
        data: chartData,
        options: {
            responsive: true,
            plugins: {
                legend: {
                    display: true,
                    position: 'top'
                }
            },
            scales: {
                y: {
                    beginAtZero: true
                }
            }
        }
    });
});
