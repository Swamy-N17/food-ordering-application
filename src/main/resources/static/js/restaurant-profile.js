const user = requireUser("RESTAURANT");

if (user) {
    renderNavbar("RESTAURANT");
}

async function loadProfile() {
    try {
        const response = await api(API.restaurant(user.id));
        const restaurant = response.data;

        document.getElementById("profileHeading").textContent =
            restaurant.name;

        document.getElementById("profileInitial").textContent =
            (restaurant.name || "F").charAt(0).toUpperCase();

        document.getElementById("rName").value =
            restaurant.name || "";

        document.getElementById("rEmail").value =
            restaurant.email || "";

        document.getElementById("rLocation").value =
            restaurant.location || "";

        document.getElementById("rRating").value =
            restaurant.rating || 5;

    } catch (error) {
        setMessage(
            document.getElementById("profileMessage"),
            error.message
        );
    }
}

document.getElementById("restaurantProfileForm").onsubmit =
    async event => {

        event.preventDefault();

        try {
            const name =
                document.getElementById("rName").value.trim();

            const email =
                document.getElementById("rEmail").value.trim();

            const location =
                document.getElementById("rLocation").value.trim();

            const rating =
                Number(document.getElementById("rRating").value);

            await api(API.updateRestaurant(user.id), {
                method: "PATCH",
                body: JSON.stringify({
                    name,
                    email,
                    location,
                    rating
                })
            });

            localStorage.setItem(
                "foodhubUser",
                JSON.stringify({
                    ...user,
                    name,
                    email
                })
            );

            document.getElementById("profileHeading")
                .textContent = name;

            setMessage(
                document.getElementById("profileMessage"),
                "Restaurant profile updated successfully.",
                "success"
            );

        } catch (error) {
            setMessage(
                document.getElementById("profileMessage"),
                error.message
            );
        }
    };

loadProfile();
