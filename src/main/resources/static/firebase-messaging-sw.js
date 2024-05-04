importScripts('https://www.gstatic.com/firebasejs/5.9.2/firebase-app.js');
importScripts('https://www.gstatic.com/firebasejs/5.9.2/firebase-messaging.js');

// Initialize Firebase
let firebaseConfig = {
    apiKey: "AIzaSyBaG-SAt2hzgha2C16CNZQGnnB0qdiH7_I",
    authDomain: "to-be-healthy-417411.firebaseapp.com",
    projectId: "to-be-healthy-417411",
    storageBucket: "to-be-healthy-417411.appspot.com",
    messagingSenderId: "827137813758",
    appId: "1:827137813758:web:a36ff6e25eb30b32eb89af",
    measurementId: "G-68SZ9TBE6Y"
};
firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();
