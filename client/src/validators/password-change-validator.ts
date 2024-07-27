export function validateOldPassword(
  oldPassword: string,
  translatedInvalidMessage: string,
) {
  return isBlank(oldPassword) ? translatedInvalidMessage : true;
}

export function validateNewPassword(
  newPassword: string,
  translatedInvalidMessage: string,
) {
  return isBlank(newPassword) ? translatedInvalidMessage : true;
}

export function validateNewPasswordConfirmation(
  newPasswordConfirmation: string,
  translatedInvalidMessage: string,
) {
  return isBlank(newPasswordConfirmation) ? translatedInvalidMessage : true;
}

function isBlank(text: string) {
  return (
    !text || text === '' || text.replaceAll(' ', '').replaceAll('\n', '') === ''
  );
}
