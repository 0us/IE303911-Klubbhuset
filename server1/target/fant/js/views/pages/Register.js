import Login from "./Login.js";

let Register = {

    render: async () => {
        //language=HTML
        let view =  /*html*/`
            <form id="reg_form">
                <div class="form-group">
                    <label for="firstname">Firstname</label>
                    <input type="text" class="form-control" name="firstname" aria-describedby=""
                           placeholder="Enter Firstname" id="firstname" required>
                </div>
                <div class="form-group">
                    <label for="lastname">Lastname</label>
                    <input type="text" class="form-control" name="lastname" aria-describedby=""
                           placeholder="Enter Lastname" id="lastname" required>
                </div>
                <div class="form-group">
                    <label for="phonenumber">Phonenumber</label>
                    <input type="text" class="form-control" name="phonenumber" aria-describedby=""
                           placeholder="Enter Phonenumber" id="phonenumber" required>
                </div>
                <div class="form-group">
                    <label for="address">Street Address</label>
                    <input type="text" class="form-control" name="address" aria-describedby=""
                           placeholder="Enter Street Address" id="address" required>
                </div>
                <div class="form-group">
                    <label for="city">City</label>
                    <input type="text" class="form-control" name="city" aria-describedby=""
                           placeholder="Enter City" id="city" required>
                </div>
                <div class="form-group">
                    <label for="postcode">Postal Code</label>
                    <input type="text" class="form-control" name="postcode" aria-describedby=""
                           placeholder="Enter Postal Code" id="postcode" required>
                </div>
                <div class="form-group">
                    <label for="email">Email address</label>
                    <input type="email" class="form-control" name="email" aria-describedby=""
                           placeholder="Enter email" id="email" required>
                </div>
                <div class="form-group">
                    <label for="uid">Username</label>
                    <input type="text" class="form-control" name="uid" aria-describedby=""
                           placeholder="Enter Username" id="uid" required>
                </div>
                <div class="form-group">
                    <label for="pwd">Password</label>
                    <input type="password" class="form-control" name="pwd"
                           placeholder="Enter Password" id="pwd" required>
                </div>
                <input type="submit" class="btn btn-primary" value="Submit">
            </form>
            <h1 id="reg_status"></h1>
        `;
        return view
    }
    , after_render: async () => {
        document.getElementById("reg_form").addEventListener("submit", async (e) => {
            e.preventDefault();
            let formElement = document.getElementById("reg_form");

            fetch('api/auth/create', {
                body: new FormData(formElement),
                method: "post"
            }).then(response => {
                const responseCode = response.status;
                if (responseCode === 200) {
                    console.log("Success!");
                    buildLogin();
                } else {
                    let statusContainer = document.getElementById("reg_status");
                    statusContainer.innerText = "Something went wrong with the registration."
                }
            });
        });
        async function buildLogin() {
            console.log("Async Success!");
            content.innerHTML = await Login.render();
            await Login.after_render();
        }
    }
};

export default Register;
