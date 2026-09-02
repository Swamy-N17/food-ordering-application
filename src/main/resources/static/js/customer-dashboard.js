const user = requireUser("CUSTOMER");

if (user) {
    renderNavbar("CUSTOMER");
}

let restaurants = [];

async function loadRestaurants() {
    const message = document.getElementById("customerMessage");

    try {
        const response = await api(API.restaurants);
        restaurants = response.data || [];
        renderRestaurants(restaurants);
    } catch (error) {
        if (error.status === 404) {
            renderRestaurants([]);
            setMessage(message, "No restaurants available yet.");
        } else {
            setMessage(message, error.message);
        }
    }
}

function renderRestaurants(list) {
    const grid = document.getElementById("restaurantGrid");

    if (!list.length) {
        grid.innerHTML = `
            <div class="empty">
                <div class="empty-icon">🍽️</div>
                <h3>No restaurants found</h3>
                <p class="muted">Try another search.</p>
            </div>
        `;
        return;
    }

    grid.innerHTML = list.map(restaurant => `
        <article class="restaurant-card">
            <div class="restaurant-visual">
                <span class="restaurant-icon">🍴</span>
                <span class="restaurant-badge">
                    ★ ${restaurant.rating || "-"}
                </span>
            </div>

            <div class="restaurant-body">
                <h3>${escapeHtml(restaurant.name)}</h3>
                <p>📍 ${escapeHtml(restaurant.location || "Local restaurant")}</p>

                <a class="btn primary"
                   href="/restaurant-menu.html?id=${restaurant.restaurantId}">
                    View menu
                </a>
            </div>
        </article>
    `).join("");
}

document.getElementById("restaurantSearch").oninput = event => {
    const query = event.target.value.toLowerCase().trim();

    renderRestaurants(
        restaurants.filter(restaurant =>
            `${restaurant.name} ${restaurant.location}`
                .toLowerCase()
                .includes(query)
        )
    );
};

loadRestaurants();
