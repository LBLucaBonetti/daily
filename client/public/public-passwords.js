const togglePasswordInputVisibility = (prefix) => {
  const input = document.querySelector(`[data-role="${prefix}-input"]`);
  const showImg = document.querySelector(`[data-role="${prefix}-show-image"]`);
  const hideImg = document.querySelector(`[data-role="${prefix}-hide-image"]`);
  if (!input || !hideImg || !showImg) {
    return;
  }
  if (input.type === 'password') {
    showImg.hidden = true;
    hideImg.hidden = false;
    input.type = 'text';
    return;
  }
  hideImg.hidden = true;
  showImg.hidden = false;
  input.type = 'password';
};

['password', 'password-confirmation'].forEach((prefix) =>
  document
    .querySelector(`[data-role="${prefix}-button"]`)
    ?.addEventListener('click', () => togglePasswordInputVisibility(prefix)),
);
