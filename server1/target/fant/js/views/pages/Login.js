import Register from "../pages/Register.js";
import AfterLogin from "../pages/after_login/AfterLogin.js";
import AddNewItemBtn from "../components/AddNewItemBtn.js";
import Navbar from "../components/Navbar.js";

let Login = {
    render: async () => {
        let view =  /*html*/`
        <form id="login_form">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text" class="form-control" id="username" aria-describedby=""
                       placeholder="Enter Username" required>
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" class="form-control" id="password" aria-describedby=""
                       placeholder="Enter Password" required>
            </div>
            <input type="submit" class="btn btn-primary" value="Login">
        </form>
      <div class="mt-4">
        <div class="d-flex justify-content-center links">
          <span>Don't have an account?</span>
          <div id="reg-btn" class="ml-2" style="color: blue">Register</div>
        </div>
<!--        <div class="d-flex justify-content-center links">-->
<!--          <a href="#">Forgot your password?</a>-->
<!--        </div>-->
      </div>
        `;
        return view
    },
    after_render: async () => {
        document.getElementById("login_form").addEventListener("submit", async (e) => {
            e.preventDefault();

            const uid = document.getElementById('username').value;
            const pwd = document.getElementById('password').value;

            fetch('api/auth/login?uid='+ uid + '&pwd=' + pwd)
                .then(response => response.text())
                .then(bearer => {
                    sessionStorage.setItem("bearer", bearer)
                    build();
                }).catch (exception => console.log(exception));
        });

        document.getElementById("reg-btn").addEventListener("click", async () => {
            content.innerHTML = await Register.render();
            await Register.after_render();
        });

        async function build() {
            // let text = document.createTextNode(document.getElementById(bearer).value);
            // document.getElementById("username_container").appendChild(text);

            // Render the new content
            await Navbar.after_render();
            content.innerHTML = await AfterLogin.render();
            await AfterLogin.after_render();

            // Remove Login button
            document.getElementById("login-btn").remove();

            loggedIn = true;
        }
    }
};

export default Login;
