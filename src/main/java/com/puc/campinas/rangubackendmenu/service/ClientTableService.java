package com.puc.campinas.rangubackendmenu.service;

import com.google.common.base.Strings;
import com.puc.campinas.rangubackendmenu.domain.ClientTable;
import com.puc.campinas.rangubackendmenu.domain.RestaurantTable;
import com.puc.campinas.rangubackendmenu.domain.data.ClientTableRequest;
import com.puc.campinas.rangubackendmenu.repository.ClientTableRepository;
import java.util.Date;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ClientTableService {

  ClientTableRepository clientTableRepository;
  RestaurantTableService restaurantTableService;

  public ClientTable startTable(ClientTableRequest request) {
    var restaurantTable = getRestaurantTable(request.getTableId());
    if (isEmpty(restaurantTable)) {
      var clientTable = createClientTable(request.getClientId(), restaurantTable);
      clientTable = clientTableRepository.save(clientTable);
      occupyRestaurantTable(restaurantTable, clientTable.getId());
      return clientTable;
    } else {
      var updateClientTable = clientTableRepository.getOne(restaurantTable.getClientTableId());
      updateClientTable.getTableMembers().add(request.getClientId());
      return clientTableRepository.save(updateClientTable);
    }
  }

  private void occupyRestaurantTable(RestaurantTable restaurantTable,String clientTableId) {
    restaurantTableService.occupyRestaurantTable(restaurantTable, clientTableId);
  }

  private ClientTable createClientTable(String clientId, RestaurantTable restaurantTable) {
    return ClientTable.builder()
        .clientId(clientId)
        .number(restaurantTable.getNumber())
        .restaurantId(restaurantTable.getRestaurantId())
        .startDateTime(new Date())
        .build();
  }

  private boolean isEmpty(RestaurantTable restaurantTable) {
    return Strings.isNullOrEmpty(restaurantTable.getClientTableId());
  }

  private RestaurantTable getRestaurantTable(String tableId) {
    return restaurantTableService.getRestaurantTable(tableId);
  }


  public ClientTable getClientTable(String tableId) {
    return clientTableRepository.findById(tableId).get();
  }
}
