import Login from "../pages/Login.js";
import AfterLogin from "../pages/after_login/AfterLogin.js";
import WelcomeScreen from "../pages/WelcomeScreen.js";

let Navbar = {
    render: async () => {
        //language=HTML
        let view =  /*html*/`
            <nav class="navbar" role="navigation" aria-label="main navigation">
                <div class="container">
                    <div class="navbar-brand">
                        <div id="logo_btn" style="cursor: pointer;" class="navbar-item">
                            <img src="/logo.png" width="112" alt="logo">
                        </div>
                    </div>
                    <div id="navbar_item_container" class="navbar-menu is-active" aria-expanded="false"></div>
                </div>
            </nav>
        `
        return view
    },
    after_render: async () => {

        const navbarContainer = document.getElementById("navbar_item_container");

        // Setup listener for logo_btn
        document.getElementById("logo_btn").addEventListener("click", async () => {
            if (loggedIn) {
                content.innerHTML = await AfterLogin.render();
                await AfterLogin.after_render();
            } else {
                content.innerHTML = await WelcomeScreen.render();
                await WelcomeScreen.after_render();
            }
        });

        // Get the bearer token
        const bearerToken = sessionStorage.getItem("bearer");
        // Check if user is authenticated using bearer token
        fetch('api/auth/currentuser', {
            method: 'GET',
            withCredentials: true,
            credentials: 'include',
            headers: {
                'Authorization': 'Bearer ' + bearerToken,
                'Content-Type': 'application/json'
            }
        })
            .then(async function (response) {
                if (response.status === 200) {
                    const jsonObject = await response.json();
                    buildUsername(jsonObject);
                } else {
                    buildLoginBtn();
                }
            });

        // Builds the html for displaying username, and displays username
        function buildUsername(json) {
            navbarContainer.innerHTML =
                /*html*/`
                <div class="navbar-item">
                    <p style="font-size: 20px;color: #354758;font-weight: bold;"
                       id="username_container">` + json.userid + `</p>
                </div>
            `;
        }

        function buildLoginBtn() {
            navbarContainer.innerHTML =
                /*html*/`
                <div class="navbar-item">
                    <div class="buttons">
                    <button id="login-btn" class="btn btn-primary">Login</button>
                    </div>
                </div>
            `;

            document.getElementById("login-btn").addEventListener("click", async () => {
                content.innerHTML = await Login.render();
                await Login.after_render();
            });
        }
    }
};

export default Navbar;
