import AfterLogin from "../pages/after_login/AfterLogin.js";
import {DEBUG} from "../../main.js";

let CreateNewPost = {
    render: async () => {
        let view =  /*html*/`
        <form id="create_new_item_form">
                <div class="form-group">
                    <label for="title">Tittel</label>
                    <input type="text" class="form-control" name="title" id="title" aria-describedby=""
                           placeholder="Skriv inn tittel" required>
                </div>
                <div class="form-group">
                    <label for="description">Beskrivelse</label>
                    <input type="text" class="form-control" name="description" id="description" aria-describedby=""
                           placeholder="Skriv inn en beskrivelse" required>
                </div>
                <div class="form-group">
                    <label for="price">Pris</label>
                    <input type="text" class="form-control" name="price" id="price" aria-describedby=""
                           placeholder="Skriv inn en pris" required>
                </div>
                <div class="form-group">
                    <label for="image">Image</label>
                    <input type="file" class="form-control" name="image" id="image" aria-describedby=""
                           placeholder="Legg til bilder" required multiple>
                </div>
                <input id="add_new_item_submit_btn" type="submit" class="btn btn-primary" value="Legg til">
            </form>
            <button id="cancel_btn" class="btn btn-secondary">Kanseller</button>
            <h1 id="create_status"></h1>
        `;
        return view
    },
    after_render: async () => {

        document.getElementById("cancel_btn").addEventListener("click", () => {
            buildAfterLogin();
        });

        document.getElementById("create_new_item_form").addEventListener("submit", (e) => {
            e.preventDefault();

            // Create a new Form Data
            let formData = new FormData();

            // Grab all the data from the form
            let title = document.getElementById("title").value;
            formData.append("title", title);
            let description = document.getElementById("description").value;
            formData.append("description", description);
            let price = document.getElementById("price").value;
            formData.append("price", price);
            let imageArray = document.getElementById("image");

            for (let i = 0; i < imageArray.files.length; i++) {
                formData.append("image" , imageArray.files[i]);
            }

            // For debugging purposes
            if (DEBUG) {
                const entries = formData.entries();
                for (let i = 0; i < imageArray.files.length + 3; i++) {
                    console.log(entries.next());
                }
            }

            createItem(formData);
        });

        async function createItem(formData) {
            const bearerToken = sessionStorage.getItem("bearer");
            await fetch('api/resource/createpost', {
                body: formData,
                method: "post",
                withCredentials: true,
                credentials: 'include',
                headers: {
                    'Authorization': 'Bearer ' + bearerToken,
                }
            }).then(response => {
                const responseCode = response.status;
                if (responseCode === 200) {
                    console.log("Success!");
                    buildAfterLogin();
                } else {
                    let statusContainer = document.getElementById("create_status");
                    statusContainer.innerText = "Something went wrong with the registration."
                }
            });
        }

        async function buildAfterLogin() {
            // Render login
            content.innerHTML = await AfterLogin.render();
            await AfterLogin.after_render();
        }
    }

};

export default CreateNewPost;
