importScripts('https://www.gstatic.com/firebasejs/5.9.2/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/5.9.2/firebase-messaging.js');

console.log("serviceWorker Start...!");

self.addEventListener("install", function (e) {
    console.log("fcm sw install..");
    self.skipWaiting();
});

self.addEventListener("activate", function (e) {
    console.log("fcm sw activate..");
});

const firebaseConfig = {
    apiKey: "AIzaSyCCvaGdXas6LuaCNPeZ9FMCNqhR5VbaL2U",
    authDomain: "solar-imprint-417411.firebaseapp.com",
    projectId: "solar-imprint-417411",
    storageBucket: "solar-imprint-417411.appspot.com",
    messagingSenderId: "793717607575",
    appId: "1:793717607575:web:fa7d5b2f29130f3c87add2",
    measurementId: "G-F501B0PBYH"
};


firebase.initializeApp(firebaseConfig);

const messaging = firebase.messaging();

messaging.setBackgroundMessageHandler((payload) => {
    console.log("setBackgroundMessageHandler Start..!");
    let notificationTitle = payload.title;
    const notificationOptions = {
        body: payload.body
    };
    self.registration.showNotification(notificationTitle, notificationOptions);
});