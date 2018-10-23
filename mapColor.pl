% Map Coloring CSP

% Declarations: % domains (i.e. colors), constraints (non-adjacent colored territories), topology
% domains (i.e. colors)
color(red).
color(green).
color(blue).

% constraints (non-adjacent colored territories)
nextto(Acolor, Bcolor) :-
  color(Acolor),
  color(Bcolor),
  Acolor \= Bcolor.

% topology
australia(WA, NT, SA, Q, NSW, V, T) :-
  nextto(WA, NT), nextto(WA, SA),
  nextto(NT, Q), nextto(NT, SA),
  nextto(Q, NSW), nextto(Q, SA),
  nextto(NSW, V), nextto(NSW, SA),
  nextto(V, SA).

% What is the query?
% australia(WA,NT,SA,Q,NSW,V,T).
% note: T (Tasmania) can be any color, so it is ignored.
