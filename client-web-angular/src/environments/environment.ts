// This file can be replaced during build by using the `fileReplacements` array.
// `ng build ---prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import { Environment } from '@delon/theme';

export const environment = {
  production: false,
  useHash: false,
  api: {
    baseUrl: 'http://127.0.0.1:8080',
    refreshTokenEnabled: false,
    refreshTokenType: 'auth-refresh'
  }
} as Environment;
