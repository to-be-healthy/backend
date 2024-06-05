importScripts('https://www.gstatic.com/firebasejs/10.4.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.4.0/firebase-messaging-compat.js');

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

debugger;
messaging.onBackgroundMessage((payload) => {
    console.log('[firebase-messaging-sw.js] Received background message ', payload);
    console.dir(payload);
    // Customize notification here
    const notificationTitle = 'Background Message Title';
    const notificationOptions = {
        body: 'Background Message body.',
        icon: '/firebase-logo.png'
    };

    self.registration.showNotification(notificationTitle, notificationOptions);
});
