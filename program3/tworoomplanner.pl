:- module( planner,
	   [
	       plan/4,change_state/3,conditions_met/2,member_state/2,
	       move/3,go/2,test/0,test2/0
	   ]).

:- [utils].

plan(State, Goal, _, Moves) :-	equal_set(State, Goal),
				write('moves are'), nl,
				reverse_print_stack(Moves).
plan(State, Goal, Been_list, Moves) :-
				move(Name, Preconditions, Actions),
				conditions_met(Preconditions, State),
				change_state(State, Actions, Child_state),
				not(member_state(Child_state, Been_list)),
				stack(Child_state, Been_list, New_been_list),
				stack(Name, Moves, New_moves),
			plan(Child_state, Goal, New_been_list, New_moves),!.

change_state(S, [], S).
change_state(S, [add(P)|T], S_new) :-	change_state(S, T, S2),
					add_to_set(P, S2, S_new), !.
change_state(S, [del(P)|T], S_new) :-	change_state(S, T, S2),
					remove_from_set(P, S2, S_new), !.
conditions_met(P, S) :- subset(P, S).

member_state(S, [H|_]) :-	equal_set(S, H).
member_state(S, [_|T]) :-	member_state(S, T).

/*moves*/
move(pickup(X), [handempty, clear(X), on(X, Y, Z), room(Z)], 
				[del(handempty), del(clear(X)), del(on(X, Y, Z)), add(clear(Y)), add(holding(X))]).

move(pickup(X), [handempty, clear(X), ontable(X, Z), room(Z)],
				[del(handempty), del(clear(X)), del(ontable(X, Z)), add(holding(X))]). 

move(putdown(X), [holding(X), room(Z)], 
					[del(holding(X)), add(ontable(X, Z)), add(clear(X)), add(handempty)]).

move(stack(X, Y), [holding(X), clear(Y), room(Z)], 
				  [del(holding(X)), del(clear(Y)), add(handempty), add(on(X, Y, Z)), add(clear(X))]). 

move(goroom1, [room(2)],
			  [del(room(2)), add(room(1))]).

move(goroom2, [room(1)], 
			  [del(room(1)), add(room(2))]). 

/*commands*/

go(S, G) :- plan(S, G, [S], []).

test :- go([handempty, ontable(b, 1), ontable(c, 1), on(a, b, 1), clear(c), clear(a), room(1)],
	          [handempty, ontable(c, 1), on(b, c, 1), on(a, b, 1), clear(a), room(1)]).

test2 :- go([handempty, ontable(b, 1), ontable(c, 1), on(a, b, 1), clear(c), clear(a), room(1)],
	          [handempty, ontable(b, 2), on(c, b, 2), on(a, c, 2), clear(a), room(2)]).