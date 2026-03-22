export function humanize(value) {
  return value.toLowerCase().replaceAll("_", " ");
}

export function encodeCredential(email, password) {
  return window.btoa(unescape(encodeURIComponent(`${email}:${password}`)));
}
