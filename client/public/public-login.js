const loginForm = document.getElementById('login-form');
const googleEmailAlertId = 'google-email-alert';
const alertIds = [
  'invalid-credentials-alert',
  'logout-alert',
  googleEmailAlertId,
];

function isGoogleEmail(email) {
  return email.value.toLowerCase().endsWith('@gmail.com');
}

function removeAlerts() {
  alertIds.forEach((alertId) => {
    const alert = document.getElementById(alertId);
    if (alert) {
      alert.remove();
    }
  });
}

function addGoogleEmailAlert() {
  // Create the element
  const googleEmailAlert = document.createElement('div');
  googleEmailAlert.setAttribute('class', 'alert alert-warning');
  googleEmailAlert.setAttribute('id', googleEmailAlertId);
  googleEmailAlert.setAttribute('role', 'alert');
  googleEmailAlert.innerHTML =
    'To log in with Google, use the appropriate button';
  // Insert into the form as first child
  loginForm.prepend(googleEmailAlert);
  // Focus on the "Log in with Google" button
  const googleLoginBtn = document.getElementById('google-login-btn');
  if (googleLoginBtn) {
    googleLoginBtn.focus();
  }
}

loginForm.addEventListener('submit', (e) => {
  const email = document.getElementById('email');
  if (isGoogleEmail(email)) {
    removeAlerts();
    addGoogleEmailAlert();
    loginForm.reset();
    e.preventDefault();
  }
});
