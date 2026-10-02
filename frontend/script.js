const BASE_URL = "http://localhost:8080";

let token = "";
let userName = "";


/* -----------------------------
   SECTION HELPERS
----------------------------- */

function hideAllSections() {

    document.getElementById("loginSection")
        .classList.add("hidden");

    document.getElementById("signupSection")
        .classList.add("hidden");

    document.getElementById("homeSection")
        .classList.add("hidden");

    document.getElementById("documentsSection")
        .classList.add("hidden");

    document.getElementById("passwordsSection")
        .classList.add("hidden");
}


function showLogin() {

    hideAllSections();

    document.getElementById("loginSection")
        .classList.remove("hidden");
}


function showSignup() {

    hideAllSections();

    document.getElementById("signupSection")
        .classList.remove("hidden");
}


function showHome() {

    hideAllSections();

    document.getElementById("homeSection")
        .classList.remove("hidden");

    document.getElementById("welcomeText")
        .textContent = "Welcome, " + userName;
}


function showDocuments() {

    hideAllSections();

    document.getElementById("documentsSection")
        .classList.remove("hidden");

    loadDocuments();
}


function showPasswords() {

    hideAllSections();

    document.getElementById("passwordsSection")
        .classList.remove("hidden");

    loadPasswords();
}


/* -----------------------------
   SIGNUP
----------------------------- */

async function signup() {

    const name =
        document.getElementById("signupName")
            .value.trim();

    const email =
        document.getElementById("signupEmail")
            .value.trim();

    const password =
        document.getElementById("signupPassword")
            .value;

    const message =
        document.getElementById("signupMessage");


    if (!name || !email || !password) {

        message.style.color = "#d32f2f";

        message.textContent =
            "Please fill all fields.";

        return;
    }


    try {

        const response =
            await fetch(
                BASE_URL + "/users/signup",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json"
                    },

                    body: JSON.stringify({
                        name: name,
                        email: email,
                        password: password
                    })
                }
            );


        if (response.ok) {

            message.style.color = "green";

            message.textContent =
                "Account created successfully!";

            document.getElementById("signupName")
                .value = "";

            document.getElementById("signupEmail")
                .value = "";

            document.getElementById("signupPassword")
                .value = "";

        } else {

            message.style.color = "#d32f2f";

            message.textContent =
                "Could not create account.";
        }

    } catch (error) {

        message.style.color = "#d32f2f";

        message.textContent =
            "Cannot connect to backend.";

        console.error(error);
    }
}


/* -----------------------------
   LOGIN
----------------------------- */

async function login() {

    const email =
        document.getElementById("loginEmail")
            .value.trim();

    const password =
        document.getElementById("loginPassword")
            .value;

    const message =
        document.getElementById("loginMessage");


    if (!email || !password) {

        message.textContent =
            "Please enter email and password.";

        return;
    }


    try {

        const url =
            BASE_URL +
            "/users/login" +
            "?email=" +
            encodeURIComponent(email) +
            "&password=" +
            encodeURIComponent(password);


        const response =
            await fetch(url);


        if (!response.ok) {

            message.textContent =
                "Invalid email or password.";

            return;
        }


        const data =
            await response.json();


        if (!data.token) {

            message.textContent =
                "Login failed.";

            return;
        }


        token = data.token;

        userName = data.name;


        localStorage.setItem(
            "vaultifyToken",
            token
        );

        localStorage.setItem(
            "vaultifyName",
            userName
        );


        document.getElementById("loginEmail")
            .value = "";

        document.getElementById("loginPassword")
            .value = "";


        showHome();

    } catch (error) {

        message.textContent =
            "Cannot connect to backend.";

        console.error(error);
    }
}


/* -----------------------------
   LOGOUT
----------------------------- */

function logout() {

    token = "";

    userName = "";

    localStorage.removeItem(
        "vaultifyToken"
    );

    localStorage.removeItem(
        "vaultifyName"
    );

    showLogin();
}


/* -----------------------------
   DOCUMENTS
----------------------------- */

async function loadDocuments() {

    try {

        const response =
            await fetch(
                BASE_URL + "/documents",
                {
                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );


        if (!response.ok) {

            document.getElementById(
                "documentsContent"
            ).innerHTML =
                "<p class='empty-message'>Could not load documents.</p>";

            return;
        }


        const documents =
            await response.json();


        displayDocuments(documents);

    } catch (error) {

        console.error(error);

        document.getElementById(
            "documentsContent"
        ).innerHTML =
            "<p class='empty-message'>Cannot connect to backend.</p>";
    }
}


async function loadDocumentsByCategory(category) {

    try {

        const response =
            await fetch(
                BASE_URL +
                "/documents/category?category=" +
                encodeURIComponent(category),
                {
                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );


        if (!response.ok) {

            document.getElementById(
                "documentsContent"
            ).innerHTML =
                "<p class='empty-message'>Could not load documents.</p>";

            return;
        }


        const documents =
            await response.json();

        displayDocuments(documents);

    } catch (error) {

        console.error(error);

        document.getElementById(
            "documentsContent"
        ).innerHTML =
            "<p class='empty-message'>Cannot connect to backend.</p>";
    }
}


async function searchDocuments() {

    const title =
        document.getElementById("documentSearch")
            .value.trim();


    if (!title) {

        loadDocuments();

        return;
    }


    try {

        const response =
            await fetch(
                BASE_URL +
                "/documents/search?title=" +
                encodeURIComponent(title),
                {
                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );


        if (!response.ok) {

            document.getElementById(
                "documentsContent"
            ).innerHTML =
                "<p class='empty-message'>Search failed.</p>";

            return;
        }


        const documents =
            await response.json();

        displayDocuments(documents);

    } catch (error) {

        console.error(error);

        document.getElementById(
            "documentsContent"
        ).innerHTML =
            "<p class='empty-message'>Cannot connect to backend.</p>";
    }
}


function displayDocuments(documents) {

    const container =
        document.getElementById(
            "documentsContent"
        );


    if (!documents ||
        documents.length === 0) {

        container.innerHTML =
            "<p class='empty-message'>No documents found.</p>";

        return;
    }


    container.innerHTML = "";


    documents.forEach(item => {

        const card =
            document.createElement("div");

        card.className = "item-card";


        card.innerHTML = `

            <h3>
                📄 ${escapeHtml(item.title)}
            </h3>

            <p>
                <strong>File:</strong>
                ${escapeHtml(item.fileName)}
            </p>

            <p>
                <strong>Category:</strong>
                ${escapeHtml(item.category)}
            </p>

            <p>
                ${escapeHtml(item.description || "")}
            </p>

            <div class="item-buttons">

                <button
                    class="download-button"
                    onclick="downloadDocument(
                        ${item.id},
                        '${escapeJs(item.fileName)}'
                    )">

                    Download

                </button>

                <button
                    class="delete-button"
                    onclick="deleteDocument(${item.id})">

                    Delete

                </button>

            </div>

        `;


        container.appendChild(card);

    });
}


async function uploadDocument() {

    const title =
        document.getElementById("documentTitle")
            .value.trim();

    const description =
        document.getElementById("documentDescription")
            .value.trim();

    const category =
        document.getElementById("documentCategory")
            .value;

    const file =
        document.getElementById("documentFile")
            .files[0];

    const message =
        document.getElementById(
            "documentMessage"
        );


    if (!title || !file) {

        message.style.color = "#d32f2f";

        message.textContent =
            "Please enter title and select a PDF.";

        return;
    }


    if (!file.name.toLowerCase().endsWith(".pdf")) {

        message.style.color = "#d32f2f";

        message.textContent =
            "Only PDF files are allowed.";

        return;
    }


    const formData =
        new FormData();

    formData.append(
        "file",
        file
    );

    formData.append(
        "title",
        title
    );

    formData.append(
        "description",
        description
    );

    formData.append(
        "category",
        category
    );


    try {

        const response =
            await fetch(
                BASE_URL + "/documents/upload",
                {
                    method: "POST",

                    headers: {
                        "Authorization":
                            "Bearer " + token
                    },

                    body: formData
                }
            );


        if (response.ok) {

            message.style.color = "green";

            message.textContent =
                "PDF uploaded successfully!";


            document.getElementById(
                "documentTitle"
            ).value = "";

            document.getElementById(
                "documentDescription"
            ).value = "";

            document.getElementById(
                "documentFile"
            ).value = "";


            loadDocuments();

        } else {

            const text =
                await response.text();

            message.style.color = "#d32f2f";

            message.textContent =
                text || "Upload failed.";
        }

    } catch (error) {

        message.style.color = "#d32f2f";

        message.textContent =
            "Cannot connect to backend.";

        console.error(error);
    }
}


async function downloadDocument(id, fileName) {

    try {

        const response =
            await fetch(
                BASE_URL +
                "/documents/" +
                id +
                "/download",
                {
                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );


        if (!response.ok) {

            alert(
                "Could not download document."
            );

            return;
        }


        const blob =
            await response.blob();


        const url =
            window.URL.createObjectURL(blob);


        const link =
            document.createElement("a");

        link.href = url;

        link.download = fileName;

        document.body.appendChild(link);

        link.click();

        link.remove();

        window.URL.revokeObjectURL(url);

    } catch (error) {

        console.error(error);

        alert("Download failed.");
    }
}


async function deleteDocument(id) {

    if (!confirm(
        "Are you sure you want to delete this document?"
    )) {

        return;
    }


    try {

        const response =
            await fetch(
                BASE_URL +
                "/documents/delete/" +
                id,
                {
                    method: "DELETE",

                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );


        if (response.ok) {

            loadDocuments();

        } else {

            alert(
                "Could not delete document."
            );
        }

    } catch (error) {

        console.error(error);

        alert("Delete failed.");
    }
}


/* -----------------------------
   PASSWORDS
----------------------------- */

async function loadPasswords() {

    try {

        const response =
            await fetch(
                BASE_URL + "/passwords",
                {
                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );


        if (!response.ok) {

            document.getElementById(
                "passwordsContent"
            ).innerHTML =
                "<p class='empty-message'>Could not load passwords.</p>";

            return;
        }


        const passwords =
            await response.json();


        displayPasswords(passwords);

    } catch (error) {

        console.error(error);

        document.getElementById(
            "passwordsContent"
        ).innerHTML =
            "<p class='empty-message'>Cannot connect to backend.</p>";
    }
}


async function loadPasswordsByCategory(category) {

    try {

        const response =
            await fetch(
                BASE_URL +
                "/passwords/category?category=" +
                encodeURIComponent(category),
                {
                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );


        if (!response.ok) {

            document.getElementById(
                "passwordsContent"
            ).innerHTML =
                "<p class='empty-message'>Could not load passwords.</p>";

            return;
        }


        const passwords =
            await response.json();

        displayPasswords(passwords);

    } catch (error) {

        console.error(error);

        document.getElementById(
            "passwordsContent"
        ).innerHTML =
            "<p class='empty-message'>Cannot connect to backend.</p>";
    }
}


async function searchPasswords() {

    const website =
        document.getElementById("passwordSearch")
            .value.trim();


    if (!website) {

        loadPasswords();

        return;
    }


    try {

        const response =
            await fetch(
                BASE_URL +
                "/passwords/search?website=" +
                encodeURIComponent(website),
                {
                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );


        if (!response.ok) {

            document.getElementById(
                "passwordsContent"
            ).innerHTML =
                "<p class='empty-message'>Search failed.</p>";

            return;
        }


        const passwords =
            await response.json();

        displayPasswords(passwords);

    } catch (error) {

        console.error(error);

        document.getElementById(
            "passwordsContent"
        ).innerHTML =
            "<p class='empty-message'>Cannot connect to backend.</p>";
    }
}


function displayPasswords(passwords) {

    const container =
        document.getElementById(
            "passwordsContent"
        );


    if (!passwords ||
        passwords.length === 0) {

        container.innerHTML =
            "<p class='empty-message'>No passwords found.</p>";

        return;
    }


    container.innerHTML = "";


    passwords.forEach(entry => {

        const card =
            document.createElement("div");

        card.className = "item-card";


        card.innerHTML = `

            <h3>
                🔐 ${escapeHtml(entry.website)}
            </h3>

            <p>
                <strong>Username:</strong>
                ${escapeHtml(entry.username)}
            </p>

            <p>
                <strong>Password:</strong>
                ••••••••
            </p>

            <p>
                <strong>Category:</strong>
                ${escapeHtml(entry.category)}
            </p>

            <p>
                ${escapeHtml(entry.notes || "")}
            </p>

            <div class="item-buttons">

                <button
                    class="view-button"
                    onclick="viewPassword(${entry.id})">

                    View Password

                </button>

                <button
                    class="delete-button"
                    onclick="deletePassword(${entry.id})">

                    Delete

                </button>

            </div>

        `;


        container.appendChild(card);

    });
}


async function savePassword() {

    const website =
        document.getElementById("website")
            .value.trim();

    const username =
        document.getElementById("username")
            .value.trim();

    const password =
        document.getElementById("savedPassword")
            .value;

    const notes =
        document.getElementById("passwordNotes")
            .value.trim();

    const category =
        document.getElementById("passwordCategory")
            .value;

    const message =
        document.getElementById(
            "passwordMessage"
        );


    if (!website ||
        !username ||
        !password) {

        message.style.color = "#d32f2f";

        message.textContent =
            "Website, username and password are required.";

        return;
    }


    try {

        const response =
            await fetch(
                BASE_URL + "/passwords/save",
                {
                    method: "POST",

                    headers: {
                        "Content-Type":
                            "application/json",

                        "Authorization":
                            "Bearer " + token
                    },

                    body: JSON.stringify({

                        website: website,

                        username: username,

                        password: password,

                        notes: notes,

                        category: category

                    })
                }
            );


        if (response.ok) {

            message.style.color = "green";

            message.textContent =
                "Password saved securely!";


            document.getElementById("website")
                .value = "";

            document.getElementById("username")
                .value = "";

            document.getElementById("savedPassword")
                .value = "";

            document.getElementById("passwordNotes")
                .value = "";


            loadPasswords();

        } else {

            const text =
                await response.text();

            message.style.color = "#d32f2f";

            message.textContent =
                text || "Could not save password.";
        }

    } catch (error) {

        message.style.color = "#d32f2f";

        message.textContent =
            "Cannot connect to backend.";

        console.error(error);
    }
}


async function viewPassword(id) {

    try {

        const response =
            await fetch(
                BASE_URL +
                "/passwords/" +
                id +
                "/view",
                {
                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );


        if (!response.ok) {

            alert(
                "Could not view password."
            );

            return;
        }


        const data =
            await response.json();


        alert(
            "Website: " +
            data.website +
            "\n\nUsername: " +
            data.username +
            "\n\nPassword: " +
            data.password +
            "\n\nNotes: " +
            (data.notes || "")
        );

    } catch (error) {

        console.error(error);

        alert(
            "Could not view password."
        );
    }
}


async function deletePassword(id) {

    if (!confirm(
        "Are you sure you want to delete this password?"
    )) {

        return;
    }


    try {

        const response =
            await fetch(
                BASE_URL +
                "/passwords/delete/" +
                id,
                {
                    method: "DELETE",

                    headers: {
                        "Authorization":
                            "Bearer " + token
                    }
                }
            );


        if (response.ok) {

            loadPasswords();

        } else {

            alert(
                "Could not delete password."
            );
        }

    } catch (error) {

        console.error(error);

        alert("Delete failed.");
    }
}


/* -----------------------------
   BASIC HTML SAFETY
----------------------------- */

function escapeHtml(value) {

    if (value === null ||
        value === undefined) {

        return "";
    }


    return String(value)
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}


function escapeJs(value) {

    return String(value)
        .replace(/\\/g, "\\\\")
        .replace(/'/g, "\\'");
}


/* -----------------------------
   SAVED LOGIN
----------------------------- */

function loadSavedLogin() {

    const savedToken =
        localStorage.getItem(
            "vaultifyToken"
        );

    const savedName =
        localStorage.getItem(
            "vaultifyName"
        );


    if (savedToken &&
        savedName) {

        token = savedToken;

        userName = savedName;

        showHome();

    } else {

        showLogin();
    }
}


loadSavedLogin();