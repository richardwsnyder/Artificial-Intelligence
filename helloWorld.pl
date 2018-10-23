/*
Universty of Central Florida
Jimmy Seeber & Richard Snyder
*/
%!      predicate(-Arg:type) is nondet
%       Predicate ...
/*  swipl - launches prolog
    halt. - quit

    consult('filename.pl').
    [filename].                       % loads a file, same as "compile buffer" from IDE
    unload_file('filename.pl').       % unloads the filename
    edit(file('filename.pl')).        % edit and save buffer

    Notes:
    variable = CAPITALLETTER or _, a wild card "_"
    everything else starts with lowercase letter
    Atom: a general-purose name with no particular meaning
    must be a lowercase letter or ('Peter') to distinguish it
    from a variable.
    ("[]") = empty list
    Term is a predicate, consists of an atom as a "functor" and zero
    or more arguments.
    functor(arg1, arg2...)
    empty list = []
    Head :- Body                    head is true if body is true
    conjunction is a comma ","
    disjunction is a semi-colon ";"
    Atoms and predicates are goals
    Facts - A clause with an empty body, an atom (constant), like "sunny."
      can also be a term (redicate) like "student(tom)."
      student(tom) :- true.
      note: student(X) is still a fact, but not bound to a particular term.

      Commands:
      assert/1                      % adds a fact or predicate in the interpreter
      retractall/1                  % remove facts and predicates
      assert(item).
      assert(category(item)).
      category(variable).           % returns item
      retractall(item).             % removes item
      retractall(category(item)).   % removes item from category
      listing.                      % lists all of the clauses currently loaded
      module_name:listing.          % lists all the predicates in the named module "likes/2"
      abolish/2                     % deletes clauses for a particular predicate
        abolish(likes, 2).
      unload_file/1                 % deletes all clauses from loaed from a particular file
        unload_file('helloWorld.pl').
      :- module(module_name [list of predicate names with arities]).
        module_name:listing.        % list only the clauses for the module


*/
/** */
/* Examples:
% sample of facts:
likes(peter, wine).
likes(peter, cheese).
likes(peter, mary).
*/

/* Example Program:
% duck(X) :- looks_like_duck(X), quacks_like_duck(X).
*/

is_a(A, B) :-
        looks_like(A, B),
        acts_like(A, B).

acts_like(duck, animal1).
acts_like(duck, animal3).
acts_like(dog, animal2).
acts_like(dog, animal4).

looks_like(duck, animal1).
looks_like(dog, animal2).
looks_like(duck, animal3).
looks_like(duck, animal4).
