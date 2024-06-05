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

Notification.requestPermission().then(function (permission) {
    const token = messaging.getToken();
    $('#token').html(token);
    return token;

}).then(async function (token) {
    $('#token').html(token);
    console.log(token);
})

messaging.onMessage((payload) => {
    console.log(payload);
});