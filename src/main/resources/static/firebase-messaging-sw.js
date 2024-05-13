importScripts('https://www.gstatic.com/firebasejs/8.10.1/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/8.10.1/firebase-messaging.js');

firebase.initializeApp({
    apiKey: "AIzaSyCCvaGdXas6LuaCNPeZ9FMCNqhR5VbaL2U",
    authDomain: "solar-imprint-417411.firebaseapp.com",
    projectId: "solar-imprint-417411",
    storageBucket: "solar-imprint-417411.appspot.com",
    messagingSenderId: "793717607575",
    appId: "1:793717607575:web:fa7d5b2f29130f3c87add2",
    measurementId: "G-F501B0PBYH"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
    console.log(
        '[firebase-messaging-sw.js] Received background message ',
        payload
    );
    // Customize notification here
    const notificationTitle = 'Background Message Title';
    const notificationOptions = {
        body: 'Background Message body.',
        icon: '/firebase-logo.png'
    };

    self.registration.showNotification(notificationTitle, notificationOptions);
});
