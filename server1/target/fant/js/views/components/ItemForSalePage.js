let ItemForSalePage = {
    render: async (item) => {
        let view =  /*html*/`
        <div id="carouselExampleControls" style="margin: auto; width: 60%;" class="carousel slide" data-ride="carousel">
            <div id="carousel-inner" class="carousel-inner">
            </div>
            <a class="carousel-control-prev" href="#carouselExampleControls" role="button" data-slide="prev">
                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                <span class="sr-only">Previous</span>
            </a>
            <a class="carousel-control-next" href="#carouselExampleControls" role="button" data-slide="next">
                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                <span class="sr-only">Next</span>
            </a>
        </div>
        <p>` + item.title + `</p>
        <p>` + item.price + `</p>
        <p>` + item.description + `</p>
        <button>Kjøp</button>
        `;
        return view
    },
    after_render: async (item) => {

        let parent = document.getElementById("carousel-inner");
        let firstIsAdded = false;

        for (let i = 0; i < item.img.length; i++) {
            let newElement = document.createElement("div");
            newElement.className = "carousel-item";

            if (firstIsAdded) {
                newElement.innerHTML =  /*html*/`
                        <img class="d-block w-100" src=" ` + item.img[i] + `" alt="slide">
                `;
            } else {
                newElement.className = "carousel-item active";
                newElement.innerHTML =  /*html*/`
                        <img class="d-block w-100" src=" ` + item.img[i] + `" alt="slide">
                `;
                firstIsAdded = true;
            }

            // Place the new element into the parent
            parent.appendChild(newElement);
        }
    }

};

export default ItemForSalePage;
