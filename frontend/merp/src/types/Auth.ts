export interface JwtPayload {
  sub: string;
  profile: string;
  tasks: string[];
  iat: number;
  exp: number;
}
