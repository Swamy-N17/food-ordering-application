const user = requireUser("RESTAURANT");

if (user) {
    renderNavbar("RESTAURANT");
}

let menu = [];
let orders = [];

// Load restaurant details
async function loadRestaurant() {
    try {
        const response = await api(API.restaurant(user.id));
        const restaurant = response.data;

        document.getElementById("restaurantName").textContent =
            restaurant.name;

        document.getElementById("restaurantLocation").textContent =
            `📍 ${restaurant.location || ""}`;

        document.getElementById("restaurantRating").textContent =
            restaurant.rating || "-";

    } catch (error) {
        setMessage(
            document.getElementById("restaurantMessage"),
            error.message
        );
    }
}

// Load restaurant menu
async function loadMenu() {
    try {
        const response = await api(API.restaurantMenu(user.id));
        menu = response.data || [];

        document.getElementById("menuCount").textContent =
            menu.length;

        const container = document.getElementById("menuTable");

        if (!menu.length) {
            container.innerHTML = `
                <div class="empty compact-empty">
                    <div class="empty-icon">🍽️</div>
                    <h3>No menu items yet</h3>
                    <p class="muted">
                        Add your first menu item.
                    </p>
                </div>
            `;
            return;
        }

        container.innerHTML = `
            <div class="table-scroll">
                <table>
                    <thead>
                        <tr>
                            <th>Item</th>
                            <th>Price</th>
                            <th>Availability</th>
                            <th>Action</th>
                        </tr>
                    </thead>

                    <tbody>
                        ${menu.map(item => `
                            <tr>
                                <td>
                                    <div class="table-item">
                                        <span>
                                            ${foodVisual(item.itemName)}
                                        </span>
                                        <b>
                                            ${escapeHtml(item.itemName)}
                                        </b>
                                    </div>
                                </td>

                                <td>
                                    ${money(item.price)}
                                </td>

                                <td>
                                    <span class="status ${
                                        item.availability
                                            ? "available"
                                            : "unavailable"
                                    }">
                                        ${
                                            item.availability
                                                ? "Available"
                                                : "Unavailable"
                                        }
                                    </span>
                                </td>

                                <td>
                                    <button
                                        class="btn small-btn"
                                        onclick="editPrice(
                                            ${item.menuItemId},
                                            ${item.price}
                                        )">
                                        Edit price
                                    </button>

                                    <button
                                        class="btn ghost small-btn"
                                        onclick="toggleAvailability(
                                            ${item.menuItemId},
                                            ${item.availability}
                                        )">
                                        ${
                                            item.availability
                                                ? "Disable"
                                                : "Enable"
                                        }
                                    </button>

                                    <button
                                        class="btn ghost small-btn delete-menu-btn"
                                        onclick="deleteMenuItem(
                                            ${item.menuItemId},
                                            '${escapeHtml(item.itemName).replace(/'/g, "\\'")}'
                                        )">
                                        Delete
                                    </button>
                                </td>
                            </tr>
                        `).join("")}
                    </tbody>
                </table>
            </div>
        `;

    } catch (error) {
        setMessage(
            document.getElementById("restaurantMessage"),
            error.message
        );
    }
}

// Format order date
function formatOrderDate(orderDateTime) {
    if (!orderDateTime) {
        return "Date not available";
    }

    try {
        return new Date(orderDateTime).toLocaleString("en-IN", {
            day: "2-digit",
            month: "short",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        });
    } catch (error) {
        return "Date not available";
    }
}

// Load restaurant orders
async function loadOrders() {
    try {
        const response = await api(API.restaurantOrders(user.id));
        orders = response.data || [];

        document.getElementById("orderCount").textContent =
            orders.length;

        document.getElementById("pendingCount").textContent =
            orders.filter(order =>
                [
                    "PLACED",
                    "CONFIRMED",
                    "PREPARING",
                    "PREPARED",
                    "OUT_FOR_DELIVERY"
                ].includes(order.status)
            ).length;

        const container =
            document.getElementById("restaurantOrders");

        if (!orders.length) {
            container.innerHTML = `
                <div class="empty">
                    <div class="empty-icon">📦</div>
                    <h3>No incoming orders</h3>
                    <p class="muted">
                        New customer orders will appear here.
                    </p>
                </div>
            `;
            return;
        }

        container.innerHTML = orders
            .slice()
            .reverse()
            .map(order => `
                <article class="partner-order">

                    <div class="partner-order-top">
                        <div>
                            <p class="eyebrow">
                                ORDER
                            </p>

                            <h3>
                                #${order.orderId}
                            </h3>

                            <small>
                                ${escapeHtml(
                                    order.customer?.name ||
                                    "Customer"
                                )}
                            </small>

                            <div class="order-meta">
                                📅 Placed on:
                                ${formatOrderDate(
                                    order.orderDateTime
                                )}
                            </div>

                            <div class="partner-delivery">
                                <span class="delivery-label">
                                    DELIVERY ADDRESS
                                </span>

                                <strong>
                                    📍 ${
                                        escapeHtml(
                                            order.deliveryAddress ||
                                            order.customer?.address ||
                                            "Address not available"
                                        )
                                    }
                                </strong>
                            </div>
                        </div>

                        <strong>
                            ${money(order.totalAmount)}
                        </strong>
                    </div>

                    <div class="order-items">
                        ${(order.orderItems || []).map(item => `
                            <div class="order-item">
                                <span>
                                    ${escapeHtml(
                                        item.menuItem?.itemName ||
                                        "Item"
                                    )}
                                    × ${item.quantity}
                                </span>

                                <b>
                                    ${money(item.subTotal)}
                                </b>
                            </div>
                        `).join("")}
                    </div>

                    <div class="partner-payment">
                        <span>
                            💳 ${
                                escapeHtml(
                                    order.payment?.paymentMethod || "-"
                                )
                            }
                        </span>

                        <span class="payment-status ${
                            String(
                                order.payment?.paymentStatus || ""
                            ).toLowerCase()
                        }">
                            ${
                                escapeHtml(
                                    order.payment?.paymentStatus || "-"
                                )
                            }
                        </span>
                    </div>

                    <div class="order-control">
                        <label>
                            Order status

                            <select
                                onchange="changeOrderStatus(
                                    ${order.orderId},
                                    this.value
                                )">

                                ${
                                    [
                                        "PLACED",
                                        "CONFIRMED",
                                        "PREPARING",
                                        "PREPARED",
                                        "OUT_FOR_DELIVERY",
                                        "DELIVERED",
                                        "CANCELLED"
                                    ].map(status => `
                                        <option
                                            value="${status}"
                                            ${
                                                order.status === status
                                                    ? "selected"
                                                    : ""
                                            }>
                                            ${status.replaceAll(
                                                "_",
                                                " "
                                            )}
                                        </option>
                                    `).join("")
                                }

                            </select>
                        </label>
                    </div>

                </article>
            `).join("");

    } catch (error) {
        if (error.status === 404) {
            document.getElementById("orderCount").textContent = "0";
            document.getElementById("pendingCount").textContent = "0";

            document.getElementById("restaurantOrders").innerHTML = `
                <div class="empty">
                    <div class="empty-icon">📦</div>
                    <h3>No incoming orders</h3>
                </div>
            `;
        } else {
            setMessage(
                document.getElementById("restaurantMessage"),
                error.message
            );
        }
    }
}

// Enable or disable a menu item
window.toggleAvailability = async function(id, current) {
    try {
        await api(API.updateMenu(id), {
            method: "PATCH",
            body: JSON.stringify({
                availability: !current
            })
        });

        loadMenu();

    } catch (error) {
        setMessage(
            document.getElementById("restaurantMessage"),
            error.message
        );
    }
};

// Edit menu item price
window.editPrice = async function(id, currentPrice) {
    const value = prompt(
        "Enter the new price:",
        currentPrice
    );

    if (value === null) {
        return;
    }

    const price = Number(value);

    if (!price || price <= 0) {
        alert("Price must be greater than 0.");
        return;
    }

    try {
        await api(API.updateMenu(id), {
            method: "PATCH",
            body: JSON.stringify({
                price
            })
        });

        loadMenu();

    } catch (error) {
        setMessage(
            document.getElementById("restaurantMessage"),
            error.message
        );
    }
};

// Delete menu item
window.deleteMenuItem = async function(id, itemName) {
    const confirmed = confirm(
        `Are you sure you want to delete "${itemName}"?`
    );

    if (!confirmed) {
        return;
    }

    try {
        await api(API.deleteMenu(id), {
            method: "DELETE"
        });

        loadMenu();

        setMessage(
            document.getElementById("restaurantMessage"),
            "Menu item deleted successfully."
        );

    } catch (error) {
        setMessage(
            document.getElementById("restaurantMessage"),
            error.message
        );
    }
};

// Change order status
window.changeOrderStatus = async function(id, status) {
    try {
        await api(API.updateOrder(id), {
            method: "PATCH",
            body: JSON.stringify({
                status
            })
        });

        loadOrders();

    } catch (error) {
        setMessage(
            document.getElementById("restaurantMessage"),
            error.message
        );

        loadOrders();
    }
};

// Open add-item modal
document.getElementById("addItemBtn").onclick = () => {
    document.getElementById("itemModal")
        .classList.remove("hidden");
};

// Close add-item modal
document.getElementById("closeItem").onclick = () => {
    document.getElementById("itemModal")
        .classList.add("hidden");
};

// Add menu item
document.getElementById("itemForm").onsubmit =
    async event => {

        event.preventDefault();

        const submitButton =
            event.target.querySelector(
                'button[type="submit"]'
            );

        // Prevent duplicate submissions
        if (submitButton.disabled) {
            return;
        }

        submitButton.disabled = true;
        submitButton.textContent = "Adding...";

        try {
            await api(API.addMenu, {
                method: "POST",
                body: JSON.stringify({
                    itemName:
                        document.getElementById("itemName")
                            .value.trim(),

                    price:
                        Number(
                            document.getElementById("itemPrice")
                                .value
                        ),

                    availability:
                        document.getElementById("itemAvailability")
                            .checked,

                    restaurant: {
                        restaurantId: user.id
                    }
                })
            });

            event.target.reset();

            document.getElementById("itemAvailability").checked =
                true;

            document.getElementById("itemModal")
                .classList.add("hidden");

            loadMenu();

        } catch (error) {
            setMessage(
                document.getElementById("restaurantMessage"),
                error.message
            );

        } finally {
            submitButton.disabled = false;
            submitButton.textContent = "Add Item";
        }
    };

// Refresh orders
const refreshOrdersButton =
    document.getElementById("refreshOrders");

refreshOrdersButton.onclick = async () => {
    refreshOrdersButton.disabled = true;
    refreshOrdersButton.textContent = "Refreshing...";

    try {
        await loadOrders();
    } finally {
        refreshOrdersButton.disabled = false;
        refreshOrdersButton.textContent = "Refresh";
    }
};

// Initial page load
loadRestaurant();
loadMenu();
loadOrders();