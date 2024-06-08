import 'package:client_flutter/app_data.dart';
import 'package:flutter/cupertino.dart';
import 'package:provider/provider.dart';

class Results extends StatelessWidget {
  const Results({super.key});

  @override
  Widget build(BuildContext context) {
    AppData appData = Provider.of<AppData>(context);
    String message = getResultMessage(appData);

    return CupertinoPageScaffold(
      navigationBar: CupertinoNavigationBar(
        middle: Text('Resultats'),
        leading: GestureDetector(
          child: Container(
            height: 44,
            width: 44,
            child: Icon(CupertinoIcons.home, size: 24,)
          ),
          onTap: () {
            appData.saveFile("rankingFile.txt", appData.ranking);
            appData.goToLobby();
          },
        ),
      ),
      child: Center(
        child: Container(
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              Text(message, style: TextStyle(color: appData.textResult, fontSize: 30, fontWeight: FontWeight.bold),),
              SizedBox(height: 50,),
              Container(
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text(appData.playersName[0]+": ", style: TextStyle(fontWeight: FontWeight.bold),),
                    Text(appData.playersScore[0].toString()+" punts"),
                    SizedBox(width: 24,),
                    Text(appData.playersName[1]+": ", style: TextStyle(fontWeight: FontWeight.bold),),
                    Text(appData.playersScore[1].toString()+" punts"),
                  ],
                ),
              ),
              SizedBox(height: 16,),
              SizedBox(
                height: 300,
                width: 300,
                child: appData.tableroResult == null ? Text('Error al mostrar la imatge del taulell resultant') : Image.memory(appData.tableroResult!),
              ),
            ]
          ),
        ),
      )
    );
  }

  String getResultMessage(AppData data){
    String result = "";

    if (data.winnerPoints == 0) {
      result = "Enemic desconectat, has guanyat";
      data.textResult = CupertinoColors.systemGreen;
    } else if (data.winnerPoints == -1) {
      result = "Empat";
    } else if (data.winner == data.playersName[0]){
      result = "Has guanyat!";
      data.textResult = CupertinoColors.activeGreen;
    } else {
      result = "Ha guanyat ${data.winner}";
      data.textResult = CupertinoColors.destructiveRed;
    }

    return result;
  }
}