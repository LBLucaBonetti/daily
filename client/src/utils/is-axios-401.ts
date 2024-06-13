import axios from 'axios';

export function isAxios401(err: unknown) {
  return axios.isAxiosError(err) && err.response?.status === 401;
}
