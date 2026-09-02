const user = requireUser("CUSTOMER");

if (user) {
    renderNavbar("CUSTOMER");
}

const params = new URLSearchParams(location.search);
const restaurantId = Number(params.get("id"));
let menuItems = [];

if (!restaurantId) {
    location.href = "/customer-dashboard.html";
}

async function loadMenu() {
    try {
        const restaurantResponse = await api(API.restaurant(restaurantId));
        const restaurant = restaurantResponse.data;

        document.getElementById("restaurantHero").innerHTML = `
            <div>
                <p class="eyebrow">RESTAURANT</p>
                <h1>${escapeHtml(restaurant.name)}</h1>
                <p>
                    📍 ${escapeHtml(restaurant.location || "")}
                    <span class="hero-rating">
                        ★ ${restaurant.rating || "-"}
                    </span>
                </p>
            </div>
            <div class="restaurant-hero-icon">🍽️</div>
        `;

        const menuResponse = await api(API.restaurantMenu(restaurantId));
        menuItems = menuResponse.data || [];

        renderMenu(menuItems);

    } catch (error) {
        setMessage(
            document.getElementById("menuMessage"),
            error.message
        );
    }
}

function renderMenu(list) {
    const grid = document.getElementById("menuGrid");

    if (!list.length) {
        grid.innerHTML = `
            <div class="empty">
                <div class="empty-icon">🍽️</div>
                <h3>No menu items available</h3>
                <p class="muted">This restaurant has not added items yet.</p>
            </div>
        `;
        return;
    }

    grid.innerHTML = list.map(item => `
        <article class="menu-card ${item.availability ? "" : "unavailable"}">

            <div class="food-visual">
                <span>${foodVisual(item.itemName)}</span>
                <small class="availability">
                    ${item.availability ? "AVAILABLE" : "UNAVAILABLE"}
                </small>
            </div>

            <div class="menu-body">
                <h3>${escapeHtml(item.itemName)}</h3>
                <p class="muted">Freshly prepared favourite.</p>

                <div class="price-row">
                    <strong class="price">${money(item.price)}</strong>

                    ${
                        item.availability
                        ? `
                            <div class="qty-add">
                                <input
                                    id="q${item.menuItemId}"
                                    type="number"
                                    min="1"
                                    value="1">
                                <button class="btn primary"
                                        onclick="addToCart(${item.menuItemId})">
                                    Add
                                </button>
                            </div>
                          `
                        : `<span class="muted">Currently unavailable</span>`
                    }
                </div>
            </div>
        </article>
    `).join("");
}

window.addToCart = function (id) {

    const item = menuItems.find(
        menuItem => menuItem.menuItemId === id
    );

    if (!item) return;

    const quantityInput =
        document.getElementById(`q${id}`);

    const quantity = Math.max(
        1,
        Number(quantityInput.value) || 1
    );

    const cart = getCart();

    // One order belongs to one restaurant.
    if (cart.length && cart[0].restaurantId !== restaurantId) {
        alert(
            "Your cart contains items from another restaurant. " +
            "Clear the cart before ordering from this restaurant."
        );
        return;
    }

    const existing = cart.find(
        cartItem => cartItem.menuItemId === id
    );

    if (existing) {
        existing.quantity += quantity;
    } else {
        cart.push({
            menuItemId: item.menuItemId,
            itemName: item.itemName,
            price: item.price,
            quantity,
            restaurantId
        });
    }

    saveCart(cart);

    quantityInput.value = 1;

    const message = document.getElementById("menuMessage");
    setMessage(
        message,
        `${item.itemName} added to your cart.`,
        "success"
    );
};

document.getElementById("menuSearch").oninput = event => {
    const query = event.target.value.toLowerCase().trim();

    renderMenu(
        menuItems.filter(item =>
            item.itemName.toLowerCase().includes(query)
        )
    );
};

loadMenu();
