import 'package:client_flutter/app_data.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class Ranking extends StatelessWidget {
  const Ranking({super.key});

  @override
  Widget build(BuildContext context) {
    AppData appData = Provider.of<AppData>(context);

    return CupertinoPageScaffold(
      navigationBar: CupertinoNavigationBar(
        middle: Text('Ranking'),
      ),
      child: Center(
        child: SafeArea(
          child: Container(
            width: MediaQuery.of(context).size.width/1.5,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.center,
              children: [
                SizedBox( height: 50,),
                Text('Records', style: TextStyle(fontWeight: FontWeight.bold),),
                Divider(),
                Container(
                  child: ListView.builder(
                    shrinkWrap: true,
                    scrollDirection: Axis.vertical,
                    itemCount: appData.ranking.length,
                    itemBuilder:(context, index) {
                      return Container(
                        padding: EdgeInsets.only(top: 2.5, bottom: 2.5),
                        child: Text(appData.ranking[index], textAlign: TextAlign.center,),
                        color: index %2 == 0 ? Colors.transparent : const Color.fromARGB(255, 191, 191, 194),
                      );
                    },
                  ),
                )
              ]
            ),
          ),
        ),
      ),
    );
  }
}