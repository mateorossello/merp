export interface JwtPayload {
  sub: string;
  id: number;
  profile: string;
  tasks: string[];
  iat: number;
  exp: number;
}
