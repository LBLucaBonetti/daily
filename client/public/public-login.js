const togglePasswordVisibility = () => {
  const passwordInput = document.getElementById('password');
  const passwordShowImg = document.getElementById('password-show');
  const passwordHideImg = document.getElementById('password-hide');
  if (passwordInput.type === 'password') {
    passwordShowImg.hidden = true;
    passwordHideImg.hidden = false;
    passwordInput.type = 'text';
    return;
  }
  passwordShowImg.hidden = false;
  passwordHideImg.hidden = true;
  passwordInput.type = 'password';
};

document
  .getElementById('toggle-password-visibility-btn')
  .addEventListener('click', togglePasswordVisibility);
