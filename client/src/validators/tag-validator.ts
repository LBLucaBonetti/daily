export function validateTag(tagText: string, translatedInvalidMessage: string) {
  return !tagText ||
    tagText === '' ||
    tagText.replaceAll(' ', '').replaceAll('\n', '') === ''
    ? translatedInvalidMessage
    : true;
}
