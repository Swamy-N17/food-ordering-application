const user = requireUser("CUSTOMER");

if (user) {
    renderNavbar("CUSTOMER");
}

async function loadProfile() {
    try {
        const response = await api(API.customer(user.id));
        const customer = response.data;

        document.getElementById("profileHeading").textContent =
            customer.name;

        document.getElementById("profileInitial").textContent =
            (customer.name || "F").charAt(0).toUpperCase();

        document.getElementById("pName").value =
            customer.name || "";

        document.getElementById("pEmail").value =
            customer.email || "";

        document.getElementById("pContact").value =
            customer.contact || "";

        document.getElementById("pAddress").value =
            customer.address || "";

    } catch (error) {
        setMessage(
            document.getElementById("profileMessage"),
            error.message
        );
    }
}

document.getElementById("customerProfileForm").onsubmit =
    async event => {

        event.preventDefault();

        try {
            await api(API.updateCustomer(user.id), {
                method: "PATCH",
                body: JSON.stringify({
                    name: document.getElementById("pName").value.trim(),
                    email: document.getElementById("pEmail").value.trim(),
                    contact: Number(
                        document.getElementById("pContact").value
                    ),
                    address: document.getElementById("pAddress").value.trim()
                })
            });

            const name =
                document.getElementById("pName").value.trim();

            const email =
                document.getElementById("pEmail").value.trim();

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
                "Profile updated successfully.",
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
