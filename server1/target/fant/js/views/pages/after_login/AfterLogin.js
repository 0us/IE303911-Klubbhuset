import ItemForSaleMenu from "../../components/ItemForSaleMenu.js";
import ItemForSalePage from "../../components/ItemForSalePage.js";
import AddNewItemBtn from "../../components/AddNewItemBtn.js";


let AfterLogin = {
    render: async () => {
        let view =  /*html*/`
        <div id="add_btn_container" class="container"></div>
        <div class="content" style="min-height: 900px;">
            <h1>Welcome to Fant</h1>
            <p>You are now successfully logged in and ready to browse the goods!</p>
            <div id="menu_item_container" class="list-group-flush">
            </div>
        </div>
        `;
        return view
    },
    after_render: async () => {
        // Render the add new item btn
        const addNewBtn = document.getElementById('add_btn_container');
        addNewBtn.innerHTML = await AddNewItemBtn.render();
        await AddNewItemBtn.after_render();

        // Create all the items to be displayed on the content page
        for (let i = 0; i < items.length; i++) {
            let textString = "menu_item_" + i;

            document.getElementById("menu_item_container").innerHTML +=
                /*html*/`<div id="` + textString + `" class="list-group-item" style="display:flex;"></div>`;
            document.getElementById(textString).innerHTML = await ItemForSaleMenu.render(items[i]);
            document.getElementById(textString).addEventListener("click", async () => {
                content.innerHTML = await ItemForSalePage.render(items[i]);
                await ItemForSalePage.after_render(items[i]);
            });
        }
    }
    // Listen for clicking action on each menu item
};

export default AfterLogin;