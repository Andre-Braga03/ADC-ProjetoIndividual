const forms = document.querySelectorAll("form[data-endpoint]");
const baseUrlInput = document.getElementById("base-url");

const baseRestUrl = new URL("./rest/", `${window.location.origin}${window.location.pathname}`).toString();
baseUrlInput.value = baseRestUrl;

const builders = {
    createAccount: (formData) => ({
        input: {
            username: formData.get("username"),
            password: formData.get("password"),
            confirmation: formData.get("confirmation"),
            phone: formData.get("phone"),
            address: formData.get("address"),
            role: formData.get("role")
        }
    }),
    login: (formData) => ({
        input: {
            username: formData.get("username"),
            password: formData.get("password")
        }
    }),
    tokenOnly: (formData) => ({
        input: {},
        token: buildToken(formData)
    }),
    deleteAccount: (formData) => ({
        input: {
            username: formData.get("username")
        },
        token: buildToken(formData)
    }),
    modifyAccount: (formData) => ({
        input: {
            username: formData.get("username"),
            attributes: compactObject({
                phone: formData.get("phone"),
                address: formData.get("address")
            })
        },
        token: buildToken(formData)
    }),
    usernameWithToken: (formData) => ({
        input: {
            username: formData.get("username")
        },
        token: buildToken(formData)
    }),
    changeUserRole: (formData) => ({
        input: {
            username: formData.get("username"),
            newRole: formData.get("newRole")
        },
        token: buildToken(formData)
    }),
    changePassword: (formData) => ({
        input: {
            username: formData.get("username"),
            oldPassword: formData.get("oldPassword"),
            newPassword: formData.get("newPassword")
        },
        token: buildToken(formData)
    })
};

forms.forEach((form) => {
    form.addEventListener("submit", async (event) => {
        event.preventDefault();

        const result = form.parentElement.querySelector(".result");
        const endpoint = form.dataset.endpoint;
        const builderName = form.dataset.builder;
        const formData = new FormData(form);
        const payload = builders[builderName](formData);

        result.textContent = "A enviar...";

        try {
            const response = await fetch(new URL(endpoint, baseRestUrl), {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify(payload)
            });

            const text = await response.text();

            try {
                result.textContent = JSON.stringify(JSON.parse(text), null, 2);
            } catch (_) {
                result.textContent = text;
            }
        } catch (error) {
            result.textContent = `Erro ao contactar o endpoint:\n${error.message}`;
        }
    });
});

function buildToken(formData) {
    return compactObject({
        tokenId: formData.get("tokenId"),
        username: formData.get("tokenUsername"),
        role: formData.get("tokenRole"),
        issuedAt: parseNumber(formData.get("issuedAt")),
        expiresAt: parseNumber(formData.get("expiresAt"))
    });
}

function parseNumber(value) {
    if (!value) {
        return undefined;
    }

    const parsed = Number(value);
    return Number.isNaN(parsed) ? undefined : parsed;
}

function compactObject(object) {
    return Object.fromEntries(
        Object.entries(object).filter(([, value]) => value !== "" && value !== undefined && value !== null)
    );
}
