let WelcomeScreen = {
    render: async () => {
        let view =  /*html*/`
            <div class="content">
                <h1>Welcome to Fant</h1>
                <p>To continue using this site, proceed with logging in from the navigation bar!</p>
            </div>
                    `;
        return view
    },
    after_render: async () => {
    }

};

export default WelcomeScreen;
