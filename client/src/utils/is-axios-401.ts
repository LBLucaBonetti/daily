import axios, { AxiosError } from 'axios';

export function isAxios401(err: Error | AxiosError | unknown) {
  return axios.isAxiosError(err) && err.response?.status === 401;
}
