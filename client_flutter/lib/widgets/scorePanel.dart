

import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';

Widget scorePanel(String titulo, String score, bool playing, bool player1) {
  bool activePlayer = playing;

  return Container(
      constraints: BoxConstraints(
        minWidth: 100,
        maxWidth: 300.0, 
      ),
      margin: EdgeInsets.all(8),
      padding: EdgeInsets.symmetric(vertical: 6, horizontal: 15),
      decoration: BoxDecoration(
        color: activePlayer ? Color.fromARGB(255, 196, 255, 194) : Color.fromARGB(255, 255, 195, 195),
        borderRadius: BorderRadius.all(Radius.circular(8)),
        border: Border.all(
          color: activePlayer ? const Color.fromARGB(255, 0, 121, 4) : const Color.fromARGB(255, 178, 12, 0),
          width: 3,
        )
      ),
      child: Column(
        children: [
          Text('$titulo', style: TextStyle(fontWeight: FontWeight.bold, fontSize: 24),),
          SizedBox(height: 6,),
          Text('$score'),
          Text('You', style: TextStyle(color: player1 ? Colors.black38 : Colors.transparent, fontSize: 10)),
        ],
      ),
    );
}


