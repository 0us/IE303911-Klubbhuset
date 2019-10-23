"use strict";

import Navbar from './views/components/Navbar.js'
import Bottombar from './views/components/Bottombar.js'
import WelcomeScreen from './views/pages/WelcomeScreen.js'

export const DEBUG = true;

// The router code. Takes a URL, checks against the list of supported routes and then renders the corresponding content page.
const buildPage = async () => {
    header.innerHTML = await Navbar.render();
    await Navbar.after_render();
    footer.innerHTML = await Bottombar.render();
    await Bottombar.after_render();
    content.innerHTML = await WelcomeScreen.render();
    await WelcomeScreen.after_render();
};

// Listen on page load:
window.addEventListener('load', buildPage);