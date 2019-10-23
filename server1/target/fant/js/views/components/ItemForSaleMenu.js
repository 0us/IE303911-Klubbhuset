let ItemForSaleMenu = {
    render: async (item) => {
        let view =  /*html*/`
            <img src="` + item.img[0] + `" width="400px" class="mr-4 p-3" 
            aria-hidden="true" alt="ItemForSale">
            <div style="display: inline-block;">
                <p id="` + item.id + `" class="pt-2 mb-0" style="font-size: 20px;
                font-weight: bold;color: #dd7e00; cursor: pointer;">` + item.title + `</p>
                <p class="mb-0 text-primary">` + item.price + ",-" + `</p>
                <p class="mb-0">` + item.description + `</p>
            </div>
            
        `;
        return view
    },
    after_render: async () => {

    }

};

export default ItemForSaleMenu;
