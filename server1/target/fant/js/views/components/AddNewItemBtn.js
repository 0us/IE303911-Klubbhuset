import CreateNewPost from "./CreateNewPost.js";

let AddNewItemBtn = {
    render: async () => {
        let view =  /*html*/`
        <div>
            <i id="add_item_btn" style="font-size: 100px;color: #65bf65; position: absolute; right: 0; padding: 20px; cursor: pointer;" class="fas fa-plus-square"></i>
        </div>
`;
        return view
    },
    after_render: async () => {
        document.getElementById("add_item_btn").addEventListener("click", async () => {
            content.innerHTML = await CreateNewPost.render();
            await CreateNewPost.after_render();
        })
    }

};

export default AddNewItemBtn;
