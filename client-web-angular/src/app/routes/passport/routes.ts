import { Routes } from '@angular/router';

import { UserLockComponent } from './lock/lock.component';
import { UserLoginComponent } from './login/login.component';
import { UserRegisterComponent } from './register/register.component';
import { UserRegisterResultComponent } from './register-result/register-result.component';

export const routes: Routes = [
  {
    path: 'login',
    component: UserLoginComponent,
    data: { title: '登录', titleI18n: 'app.login.login' }
  },
  {
    path: 'register',
    component: UserRegisterComponent,
    data: { title: '注册', titleI18n: 'app.register.register' }
  },
  {
    path: 'register-result',
    component: UserRegisterResultComponent,
    data: { title: '注册结果', titleI18n: 'app.register.register' }
  },
  {
    path: 'lock',
    component: UserLockComponent,
    data: { title: '锁屏', titleI18n: 'app.lock' }
  }
];
